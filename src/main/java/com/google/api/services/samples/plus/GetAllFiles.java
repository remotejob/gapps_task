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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juno@google.com (Your Name Here)
 *
 */
public class GetAllFiles {
  
  public static List<File> retrieveAllFiles(Drive service) throws IOException {
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

}
