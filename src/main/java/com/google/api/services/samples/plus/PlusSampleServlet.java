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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

// import com.google.api.client.util.store.FileDataStoreFactory;

/**
 * Sample Google+ servlet that loads user credentials and then shows their profile link.
 *
 * @author Nick Miceli
 */
public class PlusSampleServlet extends HttpServlet {

  private static final long serialVersionUID = 1;

  private static final String APPLICATION_NAME = "Drive API Java Quickstart";

  // private static final JacksonFactory JSON_FACTORY =
  // JacksonFactory.getDefaultInstance();



  /** Directory to store user credentials for this application. */
  // private static final java.io.File DATA_STORE_DIR = new java.io.File(
  // System.getProperty("user.home"), ".credentials/drive-java-quickstart.json");



  /** Global instance of the {@link FileDataStoreFactory}. */
  // private static FileDataStoreFactory DATA_STORE_FACTORY;



  /** Global instance of the HTTP transport. */
  // private static HttpTransport HTTP_TRANSPORT;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    final Logger log = Logger.getLogger(PlusSampleServlet.class.getName());

    Cache cache;
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      log.warning(e.getMessage());

    }

    // Check if we have stored credentials using the Authorization Flow.
    // Note that we only check if there are stored credentials, but not if they are still valid.
    // The user may have revoked authorization, in which case we would need to go through the
    // authorization flow again, which this implementation does not handle.
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

    Drive service = new Drive.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();

    FileList result = service.files().list().execute();

    List<com.google.api.services.drive.model.File> files = result.getFiles();

    List<String> filenames = new ArrayList<String>();

    for (com.google.api.services.drive.model.File file : files) {
      // respWriter.println("<p>"+file.getName()+"</p>");
      filenames.add(file.getName());

    }

    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    // put a new item in cache associated with string "headlines"
    if (!memcache.contains(clid)) {

      memcache.put(clid, filenames);

    }

    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html");

    respWriter.println(
        "<head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">");
    respWriter.println("<script src=\"//code.jquery.com/jquery-1.10.2.js\"></script>");
    respWriter.println("<script src=\"//code.jquery.com/ui/1.10.4/jquery-ui.js\"></script>");

    respWriter.println("<script src=\"js/autocompleter.js\"></script> ");

    respWriter.println(
        "<link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css\"> ");

    respWriter.println("</head>");

    respWriter.println("<div class=\"container\">");
    respWriter.println("<div class=\"well\">");

    respWriter.println("<img src='" + profile.getImage().getUrl() + "'>");
    respWriter.println("Uniq Client ID can be used as Id for DB -> " + profile.getId());
    respWriter.println("</div>");

    respWriter.println("Search from " + files.size());

    respWriter.println(
        "<div class=\"search-container\"><div class=\"ui-widget\"><input type=\"text\" id=\"search\" name=\"search\" class=\"search\" /></div>");

    // for (com.google.api.services.drive.model.File file : files) {
    // respWriter.println("<p>"+file.getName()+"</p>");
    // }
    respWriter.println("</div>");
    respWriter.close();

    // respWriter.println("<a href='" + files.size() + "'>" + "</a>");
  }
}
