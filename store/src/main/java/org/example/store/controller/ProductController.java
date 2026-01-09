package org.example.store.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.store.common.result.Result;
import org.example.store.dto.ProductQueryDTO;
import org.example.store.service.IProductService;
import org.example.store.vo.BrandCountVO;
import org.example.store.vo.CategoryCountVO;
import org.example.store.vo.ProductCardVO;
import org.example.store.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProductController {
    @Autowired
    private IProductService productService;

    // 商品列表
    @PostMapping("/list")
    public Result<IPage<ProductCardVO>> list(@RequestBody ProductQueryDTO query) {
        return Result.success(productService.getProductList(query));
    }

    // 商品详情
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        try {
            return Result.success(productService.getProductDetail(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/categories")
    public Result<List<CategoryCountVO>> categories() {
        return Result.success(productService.getCategoryCounts());
    }

    @GetMapping("/brands")
    public Result<List<BrandCountVO>> brands() {
        return Result.success(productService.getBrandCounts());
    }
}
