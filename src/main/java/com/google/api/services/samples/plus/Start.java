package com.google.api.services.samples.plus;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Start extends HttpServlet {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.print("<head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\"></head>");  
    out.print("<div class=\"container\">");
    out.print("<div class=\"well\">");
    out.print("<h1>WELCOME TO GAPPS AGGREMENT PAGE</h1>");
    out.print("<h2>Task related to eventual job aggrement</h2>");
    out.print("<a href=\"/\" class=\"btn btn-primary btn-lg\">I AGREE</a>");
    out.print("</div>");
    out.print("</div>");

    out.close();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doGet(request, response);
  }

}
