package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.store.dto.ProductQueryDTO;
import org.example.store.entity.Brand;
import org.example.store.entity.Category;
import org.example.store.entity.Product;
import org.example.store.entity.ProductSku;
import org.example.store.mapper.*;
import org.example.store.service.IProductService;
import org.example.store.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper skuMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public IPage<ProductCardVO> getProductList(ProductQueryDTO query) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        String kw = query.getKeyword();
        if (kw != null && !kw.trim().isEmpty()) {
            kw = kw.trim();

            // 品牌命中
            List<Long> brandIds = brandMapper.selectList(
                    new LambdaQueryWrapper<Brand>().like(Brand::getName, kw)
            ).stream().map(Brand::getId).toList();

            // 分类命中
            List<Long> categoryIds = categoryMapper.selectList(
                    new LambdaQueryWrapper<Category>().like(Category::getName, kw)
            ).stream().map(Category::getId).toList();

            final String finalKw = kw;
            wrapper.and(w -> {
                w.like(Product::getName, finalKw)
                        .or()
                        .like(Product::getDescription, finalKw);

                if (brandIds != null && !brandIds.isEmpty()) {
                    w.or().in(Product::getBrandId, brandIds);
                }
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    w.or().in(Product::getCategoryId, categoryIds);
                }
            });
        }

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Product::getName, query.getKeyword());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }
        if (query.getBrandId() != null) {
            wrapper.eq(Product::getBrandId, query.getBrandId());
        }

        List<Product> allProducts = productMapper.selectList(wrapper);

        // SKU
        List<Long> productIds = allProducts.stream().map(Product::getId).toList();
        List<ProductSku> allSkus = productIds.isEmpty() ? List.of() :
                skuMapper.selectList(new LambdaQueryWrapper<ProductSku>()
                        .in(ProductSku::getProductId, productIds)
                        .eq(ProductSku::getStatus, 1));

        Map<Long, List<ProductSku>> skuMap = allSkus.stream()
                .collect(Collectors.groupingBy(ProductSku::getProductId));

        // 计算最低价
        List<ProductCardVO> voList = new ArrayList<>();
        for (Product p : allProducts) {
            ProductCardVO vo = new ProductCardVO();
            BeanUtils.copyProperties(p, vo);

            List<ProductSku> skus = skuMap.get(p.getId());
            if (skus != null && !skus.isEmpty()) {
                ProductSku minSku = skus.stream()
                        .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                        .orElse(skus.get(0));
                vo.setPrice(minSku.getPrice());
                vo.setOriginalPrice(minSku.getOriginalPrice());
            } else {
                vo.setPrice(BigDecimal.ZERO);
            }

            voList.add(vo);
        }

        // 排序（全局）
        String sort = query.getSort();
        if ("price_asc".equals(sort)) {
            voList.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        } else if ("price_desc".equals(sort)) {
            voList.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        } else {
            voList.sort((a, b) -> Long.compare(b.getId(), a.getId()));
        }

        // 价格区间
        BigDecimal min = query.getMinPrice();
        BigDecimal max = query.getMaxPrice();
        if (min != null && min.compareTo(new BigDecimal("10")) > 0) {
            voList = voList.stream()
                    .filter(v -> v.getPrice() != null && v.getPrice().compareTo(min) >= 0)
                    .toList();
        }

        if (max != null && max.compareTo(new BigDecimal("10000")) < 0) {
            voList = voList.stream()
                    .filter(v -> v.getPrice() != null && v.getPrice().compareTo(max) <= 0)
                    .toList();
        }

        // 分页
        long total = voList.size();
        long current = query.getPage();
        long size = query.getSize();
        int fromIndex = (int) ((current - 1) * size);
        int toIndex = (int) Math.min(fromIndex + size, total);

        List<ProductCardVO> pageRecords =
                (fromIndex >= total) ? List.of() : voList.subList(fromIndex, toIndex);

        Page<ProductCardVO> resultPage = new Page<>(current, size, total);
        resultPage.setRecords(pageRecords);
        return resultPage;
    }


    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 获取所有SKU
        List<ProductSku> skus = skuMapper.selectList(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, id)
                .eq(ProductSku::getStatus, 1));
        vo.setSkus(skus);

        // 计算价格区间
        if (!skus.isEmpty()) {
            vo.setMinPrice(skus.stream().map(ProductSku::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            vo.setMaxPrice(skus.stream().map(ProductSku::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        }

        // 获取分类名和品牌名
        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) vo.setCategoryName(category.getName());

        Brand brand = brandMapper.selectById(product.getBrandId());
        if (brand != null) vo.setBrandName(brand.getName());

        // 获取评价数和评分
        Long cnt = reviewMapper.selectCount(new LambdaQueryWrapper<org.example.store.entity.Review>()
                .eq(org.example.store.entity.Review::getProductId, id));

        vo.setReviewCount(cnt.intValue());

        if (cnt > 0) {
            // 查出所有rating算平均（*）
            List<org.example.store.entity.Review> rs = reviewMapper.selectList(
                    new LambdaQueryWrapper<org.example.store.entity.Review>()
                            .select(org.example.store.entity.Review::getRating)
                            .eq(org.example.store.entity.Review::getProductId, id)
            );

            double avg = rs.stream().mapToInt(org.example.store.entity.Review::getRating).average().orElse(0.0);
            vo.setRating(avg);
        } else {
            vo.setRating(0.0);
        }

        return vo;
    }
    public List<CategoryCountVO> getCategoryCounts() {
        List<Category> categories = categoryMapper.selectList(null);

        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .select(Product::getCategoryId)
                .eq(Product::getStatus, 1));

        Map<Long, Long> countMap = products.stream()
                .collect(Collectors.groupingBy(Product::getCategoryId, Collectors.counting()));

        List<CategoryCountVO> result = new ArrayList<>();
        for (Category c : categories) {
            CategoryCountVO vo = new CategoryCountVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            // 获取数量
            vo.setCount(countMap.getOrDefault(c.getId(), 0L).intValue());
            result.add(vo);
        }

        return result;

    }

    public List<BrandCountVO> getBrandCounts() {
        List<Brand> brands = brandMapper.selectList(null);

        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .select(Product::getBrandId)
                .eq(Product::getStatus, 1));

        Map<Long, Long> countMap = products.stream()
                .collect(Collectors.groupingBy(Product::getBrandId, Collectors.counting()));

        List<BrandCountVO> result = new ArrayList<>();
        for (Brand b : brands) {
            BrandCountVO vo = new BrandCountVO();
            vo.setId(b.getId());
            vo.setName(b.getName());
            vo.setCount(countMap.getOrDefault(b.getId(), 0L).intValue());
            result.add(vo);
        }
        return result;
    }
}
