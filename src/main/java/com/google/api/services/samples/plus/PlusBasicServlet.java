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

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sample Google+ servlet that does a search on public activities.
 *
 * @author Nick Miceli
 */
public class PlusBasicServlet extends HttpServlet {


  private static final long serialVersionUID = 1;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // HttpTransport httpTransport = new UrlFetchTransport();
    final Logger log = Logger.getLogger(PlusBasicServlet.class.getName());

    String term = req.getParameter("term");

    log.info(term);

    if (term.length() > 2) {


      List<String> filenamesout = new ArrayList<String>();

      JsonFactory factory = new JacksonFactory();
      
//      Writer out = null;
      StringWriter sw = new StringWriter();
      JsonGenerator jGenerator = factory.createJsonGenerator(sw);
      
      
      //
      // jGenerator.writeStartArray();
      // jGenerator.writeStartObject();
      // jGenerator.writeFieldName("name");
      // jGenerator.writeString(term);
      // jGenerator.writeEndObject();
      // jGenerator.writeEndArray();
      //
      // jGenerator.close();
      //
      // log.info(sw.toString());
      
      MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

      List<String> filenames = (List<String>) memcache.get("101167552374945942416");
//      
//      JsonGenerator jGenerator = JacksonFactory.createJsonGenerator(out, JsonString.);
      

      for (String filename : filenames) {

        if (filename.toLowerCase().contains(term.toLowerCase())) {

          log.info(filename);
          
          filenamesout.add(filename);


        }

      }
      
      jGenerator.writeStartArray();
      if (filenamesout.size() > 0) {
        for (String filename : filenamesout) {
//        jGenerator.writeStartObject();
//        jGenerator.writeFieldName("name");
        jGenerator.writeString(filename );
//        jGenerator.writeEndObject();
        
        }
                
      }
      jGenerator.writeEndArray();
      jGenerator.close();
      resp.setContentType("application/json");
      resp.setStatus(200);
      Writer writer = resp.getWriter();
          
      writer.write(sw.toString());
            
      writer.close();

    } // length() > 2


  }

}
