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

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Alex Mazurov
 *
 */
public class PreviewFile extends HttpServlet {

  private static final long serialVersionUID = -5966192457639271696L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    final Logger log = Logger.getLogger(PreviewFile.class.getName());

    HttpSession session = req.getSession();

    String clid = (String) session.getAttribute("clid");
    String clidimg = (String) session.getAttribute("clidimg");
    

    String dfilename = req.getHeader("X-Dfilename");

    JsonFactory factory = new JacksonFactory();

    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    String allfilesjson = (String) memcache.get(clid);


    ObjectMapper mapper = new ObjectMapper();


    List<DfileObj> filesObj =
        Arrays.asList(mapper.readValue(allfilesjson.toString(), DfileObj[].class));

    String allfilesquant =""+filesObj.size();
    
    
    DfileObj dfileObjOut = new DfileObj();

    for (DfileObj dfileObj : filesObj) {

      if (dfileObj.getName().equals(dfilename)) {

        dfileObjOut.setId(dfileObj.getId());
        dfileObjOut.setName(dfileObj.getName());
        dfileObjOut.setMimetype(dfileObj.getMimetype());

      }

    }

    StringWriter sw = new StringWriter();
    JsonGenerator jGenerator = factory.createJsonGenerator(sw);

    jGenerator.writeStartObject();

    jGenerator.writeFieldName("clid");
    jGenerator.writeString(clid);
    jGenerator.writeFieldName("clidimg");
    jGenerator.writeString(clidimg);
    jGenerator.writeFieldName("allfilesquant");
    jGenerator.writeString(allfilesquant);
    jGenerator.writeFieldName("id");
    jGenerator.writeString(dfileObjOut.getId());
    jGenerator.writeFieldName("name");
    jGenerator.writeString(dfileObjOut.getName());
    jGenerator.writeFieldName("mimetype");
    jGenerator.writeString(dfileObjOut.getMimetype());    

    jGenerator.writeEndObject();
    jGenerator.close();
    resp.setContentType("application/json");
    resp.setStatus(200);
    Writer writer = resp.getWriter();

    writer.write(sw.toString());

    writer.close();

  }


}
