package com.quiz.category;

import com.quiz.entities.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // add category
  @PostMapping("/")
  public ResponseEntity<Category> addCategory(@RequestBody Category category) {
    Category category1 = this.categoryService.addCategory(category);
    return ResponseEntity.ok(category1);
  }

  // get category
  @GetMapping("/{categoryId}")
  public Category getCategory(@PathVariable("categoryId") Long categoryId) {
    return this.categoryService.getCategory(categoryId);
  }

  // get all categories
  @GetMapping("/")
  public ResponseEntity<?> getCategories() {
    return ResponseEntity.ok(this.categoryService.getCategories());
  }

  // update category
  @PutMapping("/")
  public Category updateCategory(@RequestBody Category category) {
    return this.categoryService.updateCategory(category);
  }

  // delete category
  @DeleteMapping("/{categoryId}")
  public void deleteCategory(@PathVariable("categoryId") Long categoryId) {
    this.categoryService.deleteCategory(categoryId);
  }
}
