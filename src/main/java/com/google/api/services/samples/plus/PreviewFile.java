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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * json service return Details for selected file
 * 
 * @author Alex Mazurov
 *
 */
public class PreviewFile extends HttpServlet {

  private static final long serialVersionUID = -5966192457639271696L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    final Logger log = Logger.getLogger(PreviewFile.class.getName());

    String jsonout;
    String allfilesjson;

    HttpSession session = req.getSession();

    String APPLICATION_NAME = (String) session.getAttribute("application_name");
    String clid = (String) session.getAttribute("clid");
    String clidimg = (String) session.getAttribute("clidimg");

    String dfilename = req.getHeader("X-Dfilename");

    JsonFactory factory = new JacksonFactory();

    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    if (!memcache.contains(clid)) {

      log.warning("memchache not exist");

      Credential credential = Utils.getCredentil(req, resp);
      allfilesjson = new GetAllFiles().filesinJSON(APPLICATION_NAME, credential, clid);
      jsonout = new GetAllFiles().DetailsSelectedFileInJson(factory, clid, clidimg, allfilesjson,
          dfilename);
      memcache.put(clid,allfilesjson);


    } else {


      allfilesjson = (String) memcache.get(clid);

      jsonout = new GetAllFiles().DetailsSelectedFileInJson(factory, clid, clidimg, allfilesjson,
          dfilename);

    }
    resp.setContentType("application/json");
    resp.setStatus(200);
    Writer writer = resp.getWriter();

    writer.write(jsonout);

    writer.close();

  }


}
