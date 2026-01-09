package org.example.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.store.dto.ProductQueryDTO;
import org.example.store.vo.BrandCountVO;
import org.example.store.vo.CategoryCountVO;
import org.example.store.vo.ProductCardVO;
import org.example.store.vo.ProductDetailVO;

import java.util.List;

public interface IProductService {
    IPage<ProductCardVO> getProductList(ProductQueryDTO query);
    ProductDetailVO getProductDetail(Long id);
    List<CategoryCountVO> getCategoryCounts();
    List<BrandCountVO> getBrandCounts();
}
