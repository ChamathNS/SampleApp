package com.sample;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleServlet extends HttpServlet {

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        String userName = req.getParameter("username");
//        String password = req.getParameter("password");
//        req.setAttribute("username", userName);
//        req.setAttribute("password", password);
//
//        RequestDispatcher requestDispatcher = req.getRequestDispatcher("home.jsp");
//        requestDispatcher.forward(req, resp);
//    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getRequestURI().endsWith("/samlsso")) {
            req.getRequestDispatcher("home.jsp").forward(req, resp);
        } else if (req.getRequestURI().endsWith("/logout")) {
            req.getRequestDispatcher("index.html").forward(req, resp);
        }
    }
}
