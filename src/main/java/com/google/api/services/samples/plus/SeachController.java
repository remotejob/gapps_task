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
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Json service return json string selected by  partial file names (or full name) files.
 *
 * @author Alex Mazurov
 * @see selectedfileinJSON
 */
public class SeachController extends HttpServlet {


  private static final long serialVersionUID = 1;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    final Logger log = Logger.getLogger(SeachController.class.getName());
    
    String jsonout;
    String allfilesjson;

    String term = req.getParameter("term");
        
    HttpSession session = req.getSession();

    String clid = (String) session.getAttribute("clid");
    String APPLICATION_NAME = (String) session.getAttribute("application_name");

    JsonFactory factory = new JacksonFactory();

    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();


    if (null == session.getAttribute("clid")) {

      log.warning("clid null???? ");

    } else {


      if (!memcache.contains(clid)) {

        log.info("memchache not exist");
                
        Credential credential = Utils.getCredentil(req, resp);
        allfilesjson= new GetAllFiles().filesinJSON(APPLICATION_NAME, credential, clid);
        jsonout = new GetAllFiles().selectedfileinJSON(factory, allfilesjson, term);
        
        memcache.put(clid,allfilesjson);
        

      } else {
                
        allfilesjson = (String) memcache.get(clid);
        jsonout = new GetAllFiles().selectedfileinJSON(factory, allfilesjson, term);

      }
      resp.setContentType("application/json");
      resp.setStatus(200);

      Writer writer = resp.getWriter();
      writer.write(jsonout);
      writer.close();
      
      
    }

  }

}
