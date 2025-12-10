package com.example.demo.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, org.springframework.ui.Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Страница не найдена");
                model.addAttribute("errorMessage", "Запрашиваемая страница не существует");
                model.addAttribute("errorCode", "404");
                return "error/custom-error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorTitle", "Доступ запрещен");
                model.addAttribute("errorMessage", "У вас недостаточно прав для доступа к этой странице");
                model.addAttribute("errorCode", "403");
                return "error/custom-error";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Внутренняя ошибка сервера");
                model.addAttribute("errorMessage", "Произошла непредвиденная ошибка");
                model.addAttribute("errorCode", "500");
                return "error/custom-error";
            }
        }

        model.addAttribute("errorTitle", "Ошибка");
        model.addAttribute("errorMessage", "Произошла ошибка");
        model.addAttribute("errorCode", "500");
        return "error/custom-error";
    }
}