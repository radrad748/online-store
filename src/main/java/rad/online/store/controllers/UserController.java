package rad.online.store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.Product;
import rad.online.store.entities.User;
import rad.online.store.services.ProductService;
import rad.online.store.services.UserService;
import rad.online.store.utils.mappers.UserMapper;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) throw new EntityException("User is null");

        model.addAttribute("user", UserMapper.getUserDto(user));
        model.addAttribute("userProducts", user.getProducts().stream()
                .sorted((p1, p2) -> p2.getDateCreated().compareTo(p1.getDateCreated()))
                .collect(Collectors.toList()));
        return "user-info";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) throw new EntityException("User is null");

        model.addAttribute("user", UserMapper.getUserDto(user));
        return "profile";
    }

    @GetMapping("/my/products")
    public String myProducts(Principal principal, Model model) {
        List<Product> userProducts =  productService.findProductsByUserEmailOrderDesc(principal.getName());
        model.addAttribute("myProducts", userProducts);

        return "my-products";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (Objects.isNull(id) || id < 0) return "Невернный запрос";

        session.invalidate();
        userService.deleteById(id);
        return "login";
    }

}
