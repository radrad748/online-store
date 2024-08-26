package rad.online.store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.Product;
import rad.online.store.entities.User;
import rad.online.store.services.ProductService;
import rad.online.store.services.UserService;
import rad.online.store.utils.PageNumbersUtil;
import rad.online.store.utils.mappers.ProductMapper;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/")
    public String products(@RequestParam(name = "searchCity", required = false) String city,
                           @RequestParam(name = "searchWord", required = false) String title, Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "12") int size,
                           @RequestParam(name = "move", defaultValue = "0") int move,
                           @RequestParam(name = "anchor", defaultValue = "0") int anchor) {
        if (page < 0 ) page = 0;

        if ((Objects.isNull(title) || title.trim().isEmpty()) && (Objects.isNull(city) || city.trim().isEmpty())) {
            List<Product> listProducts = productService.listProducts(page, size);
            List<Integer> pageNumber = PageNumbersUtil.getPageNumber(page, productService.count(), move, anchor, listProducts);

            model.addAttribute("products", listProducts);
            model.addAttribute("searchWord", "");
            model.addAttribute("searchCity", "");
            model.addAttribute("pageNumbers", pageNumber);
            model.addAttribute("currentPage", page);
        }
        else {
            List<Product> products = getProductsWithRequest(city, title.trim(), page, size);
            List<Integer> pageNumber = PageNumbersUtil.getPageNumber(page, productService.count(city, title.trim()), move, anchor, products);

            model.addAttribute("products", products);
            model.addAttribute("searchCity", city);
            model.addAttribute("pageNumbers", pageNumber);
            model.addAttribute("currentPage", page);
            if (title != null) model.addAttribute("searchWord", title);
            else model.addAttribute("searchWord", "");
        }

        return "products";
    }

    @PostMapping("/product/create")
    public String createProduct(@RequestParam("file1")MultipartFile file1,
                                           @RequestParam("file2")MultipartFile file2,
                                           @RequestParam("file3")MultipartFile file3,
                                           @Valid @ModelAttribute Product product, BindingResult errors,
                                           Principal principal, RedirectAttributes model) throws IOException {
        if (errors.hasErrors()) {
            FieldError firstError = errors.getFieldError();
            if (firstError != null) {
                model.addFlashAttribute("error", "Не корректно заполнены данные");
                return "redirect:/my/products";
            }
        }

        User user = getUserByPrincipal(principal);
        productService.saveProduct(user, product, file1, file2, file3);
        return "redirect:/my/products";
    }

    @DeleteMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/my/products";
    }

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable Long id, Model model) {
        //model.addAttribute("product", productService.getProductById(id));
        Product product = productService.getProductById(id);
        if (product == null) throw new EntityException("Product can't be null");

        model.addAttribute("product", ProductMapper.getProductDto(productService.getProductById(id)));
        model.addAttribute("user", product.getUser());

        return "product-info";
    }

    private User getUserByPrincipal(Principal principal) {
        if (principal == null) throw new EntityException("Principal doesn't must be null while user create a new product");

        return userService.findByEmail(principal.getName());
    }

    //--------------------------------------------------------------------------------------------------------------------

    private List<Product> getProductsWithRequest(String city, String title, int page, int size) {
        if (title != null && !title.trim().isEmpty() && city != null && !city.trim().isEmpty())
            return getProductsWithTitleAndCity(city, title, page, size);
        else
            return getProductsWithCityOrTitle(city, title, page, size);
    }

    private List<Product> getProductsWithCityOrTitle(String city, String title, int page, int size) {
        if (city != null && !city.trim().isEmpty())
            return productService.findByCity(city, page, size);
        else
            return productService.findByTitleOrBrandOrModel(title, page, size);
    }

    private List<Product> getProductsWithTitleAndCity(String city, String title, int page, int size) {
        return productService.findByTitleAndCity(title, city, page, size);
    }

    //------------------------------------------------------------------------------------------------------------
    /*

    private List<Integer> getPageNumber(int page, long countProducts, int move, int anchor, List<Product> listProducts) {
        List<Integer> numbers = new ArrayList<>();
        if (listProducts.isEmpty())
            return numbers;

        if (page == 0 || page == 1 || page == 2) {
            numbersFirstOfThreePages(countProducts, page, numbers);
            return numbers;
        }

        if (page > 3)
            numbersOverThreePages(countProducts, move, anchor, numbers);

        return numbers;
    }

    private void numbersFirstOfThreePages(long countProducts, int page, List<Integer> numbers) {
        if (countProducts <= 12)
            numbers.add(1);

        if (countProducts > 12 && countProducts <= 24)
            Collections.addAll(numbers, 1, 2);


        if (countProducts > 24)
            Collections.addAll(numbers, 1, 2, 3);
    }

    private void numbersOverThreePages(long countProducts, int move, int anchor, List<Integer> numbers) {
        int totalPages = (int) Math.ceil((double) countProducts / 12);

        switch (move) {
            case 0:
                moveNumbers(totalPages, anchor, numbers);
                break;
            case 1:
                if (anchor + 3 > totalPages)
                    moveNumbers(totalPages, anchor, numbers);
                else
                    moveNumbers(totalPages, anchor + 3, numbers);
                break;
            case 2:
                moveNumbers(totalPages, anchor - 3, numbers);
                break;
        }
    }

    private void moveNumbers(int totalPages, int anchor, List<Integer> numbers) {
        int pages = anchor;
        for (int i = 0; i < 3; i++) {
            if (pages > totalPages)
                break;
            numbers.add(pages);
            pages++;
        }
    }

    */
}
