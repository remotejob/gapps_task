/*
 * Copyright (c) 2013 Google Inc.
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
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Sample Google+ servlet that loads user credentials and then shows their profile link.
 *
 * @author Alex Mazurov
 */
public class PlusSampleServlet extends HttpServlet {

  private static final long serialVersionUID = 1;

  private static final String APPLICATION_NAME = "Drive API Java Quickstart";


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    final Logger log = Logger.getLogger(PlusSampleServlet.class.getName());

    HttpSession session = req.getSession();

    Cache cache;
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      log.warning(e.getMessage());

    }

    GoogleAuthorizationCodeFlow authFlow = Utils.initializeFlow();

    log.info(authFlow.getClientId());
    Credential credential = authFlow.loadCredential(Utils.getUserId(req));
    if (credential == null) {
      // If we don't have a token in store, redirect to authorization screen.
      resp.sendRedirect(
          authFlow.newAuthorizationUrl().setRedirectUri(Utils.getRedirectUri(req)).build());
      return;
    }

    // If we do have stored credentials, build the Plus object using them.
    Plus plus = new Plus.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();
    Person profile = plus.people().get("me").execute();

    String clid = profile.getId();

    session.setAttribute("clid", clid);

    Drive service = new Drive.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();

    GetAllFiles getallfile = new GetAllFiles();

    List<File> allfiles = getallfile.retrieveAllFiles(service);

    JsonFactory factory = new JacksonFactory();

    StringWriter sw = new StringWriter();

    JsonGenerator jGenerator = factory.createJsonGenerator(sw);

    jGenerator.writeStartArray();

    for (com.google.api.services.drive.model.File file : allfiles) {

      jGenerator.writeStartObject();
      jGenerator.writeFieldName("id");
      jGenerator.writeString(file.getId());
      jGenerator.writeFieldName("name");
      jGenerator.writeString(file.getName());
      jGenerator.writeEndObject();

    }

    jGenerator.writeEndArray();
    jGenerator.close();

    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();


    if (!memcache.contains(clid)) {

      log.info("memcache ! not exist");
      memcache.put(clid, sw.toString());
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
    respWriter.println("<div class=\"well\">");

    respWriter.println("<img src='" + profile.getImage().getUrl() + "'>");
    respWriter
        .println("Uniq Client ID (from +PLUS api) can be used as Id for DB <div class='redtitle'>"
            + profile.getId() + "</div> we will keep it in <div class='redtitle'>session</div>");
    respWriter.println("</div>");
    respWriter.println("<div class=\"jumbotron\" >");
    respWriter.println("Search from " + allfiles.size()
        + " files (keeped in <div class='redtitle'>memcache</div> for speed improvement)");

    respWriter.println(
        "<div class=\"search-container\"><div class=\"ui-widget\"><input type=\"text\" size=\"90%\" id=\"search\" name=\"search\" class=\"search\" /> &nbsp;&nbsp;<a class=\"btn btn-primary btn-lg\" onclick=\"getPreviewPageAsync('/previewfile');\" role=\"button\">Preview file</a></div>");

    respWriter.println("</div>");
    respWriter.println("</div>");
    respWriter.close();

  }
}
