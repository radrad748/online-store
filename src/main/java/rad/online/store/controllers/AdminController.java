package rad.online.store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rad.online.store.entities.User;
import rad.online.store.entities.enums.Role;
import rad.online.store.services.UserService;
import rad.online.store.utils.PageNumbersUtil;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping("/admin")
    public String admin(@RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "20")int size,
                        @RequestParam(name = "move", defaultValue = "0") int move,
                        @RequestParam(name = "anchor", defaultValue = "0") int anchor, Model model) {
        if (page < 0 ) page = 0;
        List<User> users = userService.findAll(page, size);
        List<Integer> pageNumber = PageNumbersUtil.getPageNumber(page, userService.count(), move, anchor, users);

        model.addAttribute("users", users);
        model.addAttribute("pageNumbers", pageNumber);
        model.addAttribute("currentPage", page);
        return "admin";
    }

    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable Long id) {
        userService.banUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/edit/{id}")
    public String userEdit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    @PostMapping("/admin/user/edit")
    public String userEdit(@RequestParam("userId") Long id, @RequestParam Map<String, String> form) {
        userService.changeUserRoles(id, form);
        return "redirect:/admin";
    }

    @PostMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (Objects.isNull(id) || id < 0) return "Невернный запрос";

        userService.deleteById(id);
        return "redirect:/admin";
    }

}
