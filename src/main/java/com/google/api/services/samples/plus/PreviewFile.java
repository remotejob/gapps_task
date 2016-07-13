/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.plus;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author juno@google.com (Your Name Here)
 *
 */
public class PreviewFile extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -5966192457639271696L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    final Logger log = Logger.getLogger(PreviewFile.class.getName());

    HttpSession session = req.getSession();

    String clid = (String) session.getAttribute("clid");
    
    resp.setContentType("text/html");
    resp.setStatus(200);
    Writer writer = resp.getWriter();
 
    writer.write("<link href='http://fonts.googleapis.com/css?family=Finger+Paint' rel='stylesheet' type='text/css'>");
    writer.write(
        "<head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
    writer.write("</head>");

    writer.write("<div class=\"container\">");
    writer.write("<div class=\"well\">");
    
    writer.write("Cliid ->"+clid );
    
    writer.write("</div>");
    writer.write("</div>");
    writer.close();

  }


}
