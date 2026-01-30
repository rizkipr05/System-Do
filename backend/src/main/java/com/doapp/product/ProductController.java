package com.doapp.product;

import com.doapp.product.dto.ProductDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductRepository productRepo;

  public ProductController(ProductRepository productRepo) {
    this.productRepo = productRepo;
  }

  @GetMapping
  public List<ProductDto> list() {
    return productRepo.findByActiveTrueOrderByNameAsc()
        .stream()
        .map(p -> new ProductDto(p.getId(), p.getName(), p.getSku(), p.getUnit(), p.getPrice()))
        .toList();
  }
}
