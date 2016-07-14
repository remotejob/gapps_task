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
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.appengine.repackaged.org.codehaus.jackson.JsonParseException;
import com.google.appengine.repackaged.org.codehaus.jackson.map.JsonMappingException;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author juno@google.com (Your Name Here)
 *
 */
public class GetAllFiles {
  
  public  List<File> retrieveAllFiles(Drive service) throws IOException {
    List<File> result = new ArrayList<File>();
    Files.List request = service.files().list();

    do {
      try {
        FileList files = request.execute();

        result.addAll(files.getFiles());
        request.setPageToken(files.getNextPageToken());
      } catch (IOException e) {
        System.out.println("An error occurred: " + e);
        request.setPageToken(null);
      }
    } while (request.getPageToken() != null &&
             request.getPageToken().length() > 0);

    return result;
  }
  
  public String filesinJSON(String APPLICATION_NAME,Credential credential,String clid) throws IOException {
 
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
      jGenerator.writeFieldName("mimetype");
      jGenerator.writeString(file.getMimeType());
      jGenerator.writeEndObject();

    }

    jGenerator.writeEndArray();
    jGenerator.close();
    
    return sw.toString();    
    
  }
  
  public String selectedfileinJSON(JsonFactory factory,String allfilesjson,String term) throws IOException {
   
    JsonParser jParser = factory.createJsonParser(allfilesjson);

   
    List<String> filenamesout = new ArrayList<String>();
    
    while (jParser.nextToken() != JsonToken.END_ARRAY) {

      String fieldname = jParser.getCurrentName();

      if ("name".equals(fieldname)) {

        jParser.nextToken();

        String filename = jParser.getText();

        if (filename.toLowerCase().contains(term.toLowerCase())) {

          filenamesout.add(filename);

        }

      }

    }

    jParser.close();

    StringWriter sw = new StringWriter();
    JsonGenerator jGenerator = factory.createJsonGenerator(sw);

    jGenerator.writeStartArray();
    if (filenamesout.size() > 0) {
      for (String filename : filenamesout) {

        jGenerator.writeString(filename);

      }

    }
    jGenerator.writeEndArray();
    jGenerator.close();
    
    return sw.toString();
          
  }

  public String DetailsSelectedFileInJson(JsonFactory factory,String clid,String clidimg,String allfilesjson,String selectedfilename) throws JsonParseException, JsonMappingException, IOException{
 
    ObjectMapper mapper = new ObjectMapper();


    List<DfileObj> filesObj =
        Arrays.asList(mapper.readValue(allfilesjson.toString(), DfileObj[].class));

    String allfilesquant =""+filesObj.size();
    
    
    DfileObj dfileObjOut = new DfileObj();

    for (DfileObj dfileObj : filesObj) {

      if (dfileObj.getName().equals(selectedfilename)) {

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
    
    
    return sw.toString();
    
   
  }
  
   
  
}
