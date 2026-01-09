package org.example.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.entity.UserAddress;
import org.example.store.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.time.ZoneId;

@RestController
@RequestMapping("/user/addresses")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AddressController {

    @Autowired
    private UserAddressMapper userAddressMapper;

    // 查询地址列表（默认 is_deleted=0）
    @GetMapping
    public Result<List<UserAddress>> list() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        LambdaQueryWrapper<UserAddress> qw = new LambdaQueryWrapper<>();
        qw.eq(UserAddress::getUserId, currentUserId)
          .eq(UserAddress::getIsDeleted, 0)
          .orderByDesc(UserAddress::getIsDefault)
          .orderByDesc(UserAddress::getCreatedAt);

        List<UserAddress> addresses = userAddressMapper.selectList(qw);
        return Result.success(addresses);
    }

    // 新增地址
    @PostMapping
    public Result<String> create(@RequestBody UserAddress address) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        if (address.getReceiverName() == null || address.getPhone() == null ||
            address.getRegion() == null || address.getDetail() == null) {
            return Result.error("参数错误：收货人姓名、电话、地区和详细地址不能为空");
        }

        // 用户ID和创建时间
        address.setUserId(currentUserId);
        address.setIsDeleted(0);
        address.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        address.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        // 其他默认取消
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            unsetDefaultForUser(currentUserId);
        } else {
            address.setIsDefault(0);
        }

        userAddressMapper.insert(address);
        return Result.success("地址添加成功");
    }

    // 编辑地址
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody UserAddress address) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        // 原地址
        UserAddress originalAddress = userAddressMapper.selectById(id);
        if (originalAddress == null || !originalAddress.getUserId().equals(currentUserId)) {
            return Result.error("地址不存在或无权限操作");
        }

        if (originalAddress.getIsDeleted() == 1) {
            return Result.error("地址已被删除");
        }

        // 更新时间
        address.setId(id);
        address.setUserId(currentUserId);
        address.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        if (address.getReceiverName() != null) originalAddress.setReceiverName(address.getReceiverName());
        if (address.getPhone() != null) originalAddress.setPhone(address.getPhone());
        if (address.getRegion() != null) originalAddress.setRegion(address.getRegion());
        if (address.getDetail() != null) originalAddress.setDetail(address.getDetail());
        if (address.getIsDefault() != null) {
            if (Integer.valueOf(1).equals(address.getIsDefault())) {
                unsetDefaultForUser(currentUserId);
                originalAddress.setIsDefault(1);
            } else {
                originalAddress.setIsDefault(0);
            }
        }

        originalAddress.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        userAddressMapper.updateById(originalAddress);
        return Result.success("地址更新成功");
    }

    // 设为默认（其它 default 置 0）
    @PutMapping("/{id}/default")
    public Result<String> setDefault(@PathVariable Long id) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        // 查询地址
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(currentUserId)) {
            return Result.error("地址不存在或无权限操作");
        }

        if (address.getIsDeleted() == 1) {
            return Result.error("地址已被删除");
        }

        unsetDefaultForUser(currentUserId);
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getId, id)
                    .set(UserAddress::getIsDefault, 1)
                    .set(UserAddress::getUpdatedAt, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        userAddressMapper.update(updateWrapper);

        return Result.success("已设为默认地址");
    }

    // 软删除（is_deleted=1）
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(currentUserId)) {
            return Result.error("地址不存在或无权限操作");
        }

        if (address.getIsDeleted() == 1) {
            return Result.error("地址已被删除");
        }

        // 最新未删除->默认
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            setNewDefaultAddress(currentUserId, id);
        }

        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getId, id)
                    .set(UserAddress::getIsDeleted, 1)
                    .set(UserAddress::getUpdatedAt, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        userAddressMapper.update(updateWrapper);

        return Result.success("地址删除成功");
    }

    // 取消所有默认
    private void unsetDefaultForUser(Long userId) {
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getUserId, userId)
                    .set(UserAddress::getIsDefault, 0);
        userAddressMapper.update(updateWrapper);
    }

    // 最新未删默认
    private void setNewDefaultAddress(Long userId, Long deletedAddressId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                   .eq(UserAddress::getIsDeleted, 0)
                   .ne(UserAddress::getId, deletedAddressId)
                   .orderByDesc(UserAddress::getCreatedAt)
                   .last("LIMIT 1");

        UserAddress newDefaultAddress = userAddressMapper.selectOne(queryWrapper);
        if (newDefaultAddress != null) {
            LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserAddress::getId, newDefaultAddress.getId())
                        .set(UserAddress::getIsDefault, 1);
            userAddressMapper.update(updateWrapper);
        }
    }
}
