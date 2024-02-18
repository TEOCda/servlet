package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Long.*;

public class MainServlet extends HttpServlet {
    PostController postController;
    public static final String API_POSTS = "/api/posts";
    public static final String API_POSTS_D = "/api/posts/\\d+";
    public static final String STR = "/";
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String DELETE_METHOD = "DELETE";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        postController = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse response) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            if (method.equals(GET_METHOD) && path.equals(API_POSTS)) {
                postController.all(response);
                return;
            }
            if (method.equals(GET_METHOD) && path.matches(API_POSTS_D)) {
                final var id = parseLong(path.substring(path.lastIndexOf(STR) + 1));
                postController.getById(id, response);
                return;
            }
            if (method.equals(POST_METHOD) && path.equals(API_POSTS)) {
                postController.save(req.getReader(), response);
            }
            if (method.equals(DELETE_METHOD) && path.matches(API_POSTS_D)) {
                final var id = parseLong(path.substring(path.lastIndexOf(STR) + 1));
                postController.removeById(id, response);
            }
            response.setStatus(response.SC_OK);
        } catch (NotFoundException e) {
            e.getMessage();
            response.setStatus(response.SC_NOT_FOUND);
        } catch (IOException ioException) {
            ioException.getMessage();
            response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

