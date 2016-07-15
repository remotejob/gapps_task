/*
 * Copyright (c) 2016 Gapps OY.
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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Mains servlet to load main page.
 * on start and reload check  memcache contents as well CLIENT ID 
 *
 * @author Alex Mazurov
 * @custom.todo 1)Button to cleanUP 2)Check if search was null inform user what files not found 
 * 
 */
public class MainServlet extends HttpServlet {

  private static final long serialVersionUID = 1;

  private static final String APPLICATION_NAME = "Drive API Java Gapps Task";


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    final Logger log = Logger.getLogger(MainServlet.class.getName());

    HttpSession session = req.getSession();
    
    session.setAttribute("application_name", APPLICATION_NAME);
    
    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    String clid;
    String clidimg;
    Credential credential = null;
    Person profile = null;

    credential = Utils.getCredentil(req, resp);


    if (null == session.getAttribute("clid")) {

      log.info("clid null");
      // If we do have stored credentials, build the Plus object using them.
      Plus plus = new Plus.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
          .setApplicationName(APPLICATION_NAME).build();
      profile = plus.people().get("me").execute();

      clid = profile.getId();
      clidimg = profile.getImage().getUrl();

      session.setAttribute("clid", clid);
      session.setAttribute("clidimg", clidimg);

      if (!memcache.contains(clid)) {

        log.info("!memcache.contains() "+clid);
        
        String jsonout = new GetAllFiles().filesinJSON(APPLICATION_NAME, credential, clid); 
        
        memcache.put(clid,jsonout);
      }


    } else {

      clid = (String) session.getAttribute("clid");
      
      if (!memcache.contains(clid)) {

        log.info("no memcache for "+clid);
        
        String jsonout = new GetAllFiles().filesinJSON(APPLICATION_NAME, credential, clid);
        memcache.put(clid,jsonout);
      
      }
            
    }
      

    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html");
    respWriter.println(
        "<link href='http://fonts.googleapis.com/css?family=Finger+Paint' rel='stylesheet' type='text/css'>");
    respWriter.println(
        "<head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
    respWriter.println("<script src=\"//code.jquery.com/jquery-1.10.2.js\"></script>");
    respWriter.println("<script src=\"//code.jquery.com/ui/1.10.4/jquery-ui.js\"></script>");

    respWriter.println("<script src=\"js/autocompleter.js\"></script> ");

    respWriter.println(
        "<link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css\"> ");

    respWriter.println("<link rel=\"stylesheet\" href=\"/styles/main.css\" />");
    respWriter.println("</head>");

    respWriter.println("<div class=\"container\">");
    respWriter
        .println("<img src=\"/img/blog-gcp-logo.png\" alt=\"Appengine\" style=\"height:20%;\">");
    respWriter.println("<div class=\"well\">");
    respWriter.println(
        "<div id ='clidimg'><img src=\"/img/opengapps.png\" alt=\"Appengine\" style=\"height:20%;\">GAPPS TASK ADS quick search files from Google Drive (.jpg files viewer) </div>");

    respWriter.println("</div>");
    respWriter.println("<div class=\"jumbotron\" >");
    respWriter.println("Search file in Drive"
        + " (all files keeped in <div class='redtitle'>memcache</div> for speed improvement)");

    respWriter.println(
        "<div class=\"search-container\"><div class=\"ui-widget\"><input type=\"text\" size=\"90%\" id=\"search\" name=\"search\" class=\"search\" /> &nbsp;&nbsp;<a class=\"btn btn-primary btn-lg\" onclick=\"getPreviewPageAsync('/previewfile');\" role=\"button\">Preview file</a></div>");

    respWriter.println("<h4>file ID <span id = \"id\" class=\"label label-danger\">id</span></h4>");
    respWriter
        .println("<h4>NAME<span id = \"name\" class=\"label label-warning\">name</span></h4>");
    respWriter.println(
        "<h4>MimeType<span id = \"mimetype\" class=\"label label-default\">mimetype</span></h4>");

    respWriter.println("<div id=\"showimage\"></div>");

    respWriter.println("</div>");
    respWriter.println("</div>");
    respWriter.close();

  }
}
