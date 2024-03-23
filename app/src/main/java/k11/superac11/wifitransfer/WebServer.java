package k11.superac11.wifitransfer;



import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import android.util.Base64;

import androidx.annotation.DrawableRes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
    private final MainActivity activity;
    String ipadd="1.1.1.1";
    private static final String TAG = "WebServer";
    private static final int PORT = 8080;

    public WebServer(String ip, MainActivity activity) throws IOException {
        super(ip, PORT);
        ipadd=ip;
        Log.i(TAG, "Server running at " + ip + ":" + PORT);
        start();

        this.activity=activity;
        logHelp("Server Started at: "+ip+":8080\n" );

        logHelp("** Don't close the App during transfer **\n\nEnter the IP address on web browser, or scan the QR Code on other devices to browse the files. \n\nEnsure both devices are on the same network or use a mobile hotspot to start the server & browse files");



    }

    // File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());


    public void logHelp(String uri) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.writeToLog(uri);
            }
        });
    }



    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(TAG, method + " '" + uri + "' ");
        final String forLog=uri;

        // Set the default file to index.html
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        //  File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        File requestedFile = new File(rootDir, uri);
        if (method == Method.POST) {



// Handle file upload
            try {
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                Log.d(TAG, "Received Files: " + files);

                List<String> filepaths = new ArrayList<>(files.values());

                if (filepaths.isEmpty()) {
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "No files provided in the request.");
                }

                File targetDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Wifi File Manager Upload");
                if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error creating the target directory.");
                }

                Map<String, List<String>> parameters = session.getParameters();
                List<String> filenames = parameters.get("filenames");
                List<String> filesizes = parameters.get("filesizes");

                if (filenames != null && !filenames.isEmpty() && filesizes != null && !filesizes.isEmpty() && filenames.size() == filepaths.size() && filesizes.size() == filepaths.size()) {
                    for (String filepath : filepaths) {
                        File uploadedFile = new File(filepath);

                        // Iterate through the received files to find the matching file size dynamically
                        for (int i = 0; i < filesizes.size(); i++) {
                            long expectedFileSize = Long.parseLong(filesizes.get(i));
                            Log.d(TAG, "File: " + uploadedFile.getName() + ", Expected Size: " + expectedFileSize + ", Actual Size: " + uploadedFile.length());

                            // Check if the actual file size matches the expected size
                            if (uploadedFile.length() == expectedFileSize) {
                                String originalFilename = filenames.get(i);
                                String uniqueFileName= getUniqueFilename(targetDirectory, originalFilename);

                                File destFile = new File(targetDirectory, uniqueFileName);

                                try (FileInputStream fis = new FileInputStream(uploadedFile);
                                     FileOutputStream fos = new FileOutputStream(destFile)) {
                                    byte[] buffer = new byte[1024];
                                    int read;
                                    while ((read = fis.read(buffer)) != -1) {
                                        fos.write(buffer, 0, read);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error copying files");
                                }

                                logHelp(destFile.toString());
                                break;  // Break the inner loop once a match is found
                            }
                        }
                    }

                    Log.d(TAG, "Files uploaded successfully. Uploaded files can be accesses at Downloads/Wifi File Manager Upload");
                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "Files uploaded successfully!");
                } else {
                    Log.e(TAG, "Mismatch between file data, filenames, or filesizes.1223");
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Mismatch between file data, filenames, or filesizes.");
                }
            } catch (IOException | ResponseException e) {
                Log.e(TAG, "IOException while handling file upload: " + e.getMessage());
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error 500: " + e.getMessage());
            }



        }
        else if (method == Method.GET && requestedFile.exists()) {
            if (requestedFile.isDirectory()) {
                // Redirect to directory listing page
                StringBuilder sb = new StringBuilder();
                sb.append("<!DOCTYPE html>\n<html><head>");
                sb.append("    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Wifi Files Browser</title>");
                sb.append("    <style>\n" +
                        "       {box-sizing: border-box;}body {margin: 0;padding: 0;font-family: \"Roboto\", sans-serif;font-size: 16px;line-height: 1.5;color: #333;background-color: #f5f5f5;}.container {max-width: 600px;margin: 0 auto;padding: 20px;}h2 {margin: 0;padding: 10px;font-size: 24px;font-weight: bold;color: #fff;background-color: #3f51b5;}form {display: inline;flex-direction: column;align-items: center;margin-top: 20px;}input[type=\"file\"] {margin: 10px 0;padding: 10px;border: 2px dashed #ccc;border-radius: 5px;cursor: pointer;}input[type=\"submit\"] {padding: 10px 30px;border: none;border-radius: 5px;background-color: #3f51b5;color: #fff;font-weight: bold;cursor: pointer;}button {margin-top: 10px;padding: 10px 20px;border: none;border-radius: 5px;background-color: #ccc;color: #333;font-weight: bold;cursor: pointer;}button:hover {background-color: #ddd;}ul {list-style: none;margin: 20px 0 0 0;padding: 0;}li {display: flex;justify-content: space-between;align-items: center;padding: 10px;border-bottom: 1px solid #ddd;}li:first-child {border-top: 1px solid #ddd;}a {text-decoration: none;color: #3f51b5;font-weight: bold;}a[dir] {color: #009688;}\n" +
                        "    </style>");
                sb.append("</head><body> <div class=\"container\">");

// File upload form
                sb.append("<style>body{font-family:Roboto,sans-serif;background-color:#f0f0f0;margin:20px}#file-selection{background-color:#fff;padding:20px;border-radius:8px;box-shadow:0 0 10px rgba(0,0,0,.1);display:flex;flex-direction:column;align-items:center}#file-input{display:none}#file-label{background-color:#3498db;color:#fff;padding:10px 20px;border-radius:5px;cursor:pointer;display:flex;align-items:center}#file-label:hover{background-color:#2980b9}#file-list{width:100%;border-collapse:collapse;margin-top:10px}table,th,td{border:1px solid #ddd}td,th{padding:15px;text-align:left}th{background-color:#f2f2f2}.remove-button{background-color:#e74c3c;color:#fff;border:none;padding:8px 15px;border-radius:4px;cursor:pointer}.remove-button:hover{background-color:#c0392b}.upload-button-container{display:flex;flex-direction:column;align-items:center}.upload-button{background-color:#2ecc71;color:#fff;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;margin-top:10px;pointer-events:none;opacity:0.5}.upload-button:hover{background-color:#27ae60}.uploading .upload-button,.upload-complete .upload-button{pointer-events:none;opacity:0.5}.uploading .upload-button:hover,.upload-complete .upload-button:hover{background-color:#2ecc71}.progress-bar-container{width:90%;display:flex;flex-direction:row;align-items:center;margin-top:10px}.progress-bar{flex:1;height:20px;background-color:#ddd;border-radius:4px;overflow:hidden;position:relative}.progress-bar-inner{height:100%;background-color:#4caf50;width:0;transition:width 0.3s ease-in-out}.stop-button-container{width:10%;margin-left:10px;display:flex;align-items:center;display:none}.stop-button{background-color:#e74c3c;color:#fff;border:none;padding:10px 10px;border-radius:5px;cursor:pointer}.stop-button:hover{background-color:#c0392b}.upload-complete-message{margin-top:10px;color:#2ecc71}h3{align-self:flex-start;margin-bottom:5px}</style>");
                sb.append("<div id=\"file-selection\" class=\"upload-button-container\">");
                sb.append("  <h3>Upload Files</h3>");
                sb.append("<input type=\"file\" id=\"file-input\" name=\"files[]\" multiple>");
                sb.append("  <label id=\"file-label\" for=\"file-input\">");
                sb.append("    <span style=\"margin-right: 10px;\">+</span> Add Files.");
                sb.append("  </label>");
                sb.append("  <table id=\"file-list\">");
                sb.append("    <tr>");
                sb.append("      <th>File Name</th>");
                sb.append("      <th>Actions</th>");
                sb.append("    </tr>");
                sb.append("  </table>");
                sb.append("  <div class=\"progress-bar-container\">");
                sb.append("    <div class=\"progress-bar\">");
                sb.append("      <div class=\"progress-bar-inner\"></div>");
                sb.append("    </div>");
                sb.append("    <div class=\"stop-button-container\">");
                sb.append("      <button class=\"stop-button\" onclick=\"stopUpload()\">Stop</button>");
                sb.append("    </div>");
                sb.append("  </div>");
              //  sb.append("  <button class=\"upload-button\" onclick=\"uploadFiles()\">Upload Inside</button>");

             sb.append("  <button class=\"upload-button\" >Upload </button>");
                sb.append("  <div class=\"upload-complete-message\" id=\"upload-complete-message\"></div>");
                sb.append("</div>");
                sb.append("<script>");
                sb.append("document.addEventListener('DOMContentLoaded', function () {");
                sb.append("  const fileInput = document.getElementById('file-input');");
                sb.append("  const fileList = document.getElementById('file-list');");
                sb.append("  const progressBar = document.querySelector('.progress-bar-inner');");
                sb.append("  const stopButtonContainer = document.querySelector('.stop-button-container');");
                sb.append("  const uploadCompleteMessage = document.getElementById('upload-complete-message');");
                sb.append("  let xhr;");
                sb.append("  fileInput.addEventListener('change', function (event) {");
                sb.append("    const files = event.target.files;");
                sb.append("    const uploadButton = document.querySelector('.upload-button');");
                sb.append("    if (files.length > 0) {");
                sb.append("      uploadButton.style.pointerEvents = 'auto';");
                sb.append("      uploadButton.style.opacity = '1';");
                sb.append("    } else {");
                sb.append("      uploadButton.style.pointerEvents = 'none';");
                sb.append("      uploadButton.style.opacity = '0.5';");
                sb.append("    }");
                sb.append("    for (let i = 0; i < files.length; i++) {");
                sb.append("      const file = files[i];");
                sb.append("      const row = fileList.insertRow(-1);");
                sb.append("      const cell1 = row.insertCell(0);");
                sb.append("      cell1.textContent = file.name;");
                sb.append("      const fileSize = file.size;");
                sb.append("      cell1.textContent += ' (' + formatBytes(fileSize) + ')';");
                sb.append("      const cell2 = row.insertCell(1);");
                sb.append("      const removeButton = document.createElement('button');");
                sb.append("      removeButton.textContent = 'Remove';");
                sb.append("      removeButton.classList.add('remove-button');");
                sb.append("      removeButton.addEventListener('click', function () {");
                sb.append("        fileList.deleteRow(row.rowIndex);");
                sb.append("        fileInput.value = null;");
                sb.append("        const uploadButton = document.querySelector('.upload-button');");
                sb.append("        uploadButton.style.pointerEvents = 'none';");
                sb.append("        uploadButton.style.opacity = '0.5';");
                sb.append("      });");
                sb.append("      cell2.appendChild(removeButton);");
                sb.append("    }");
                sb.append("  });");
                sb.append("  const uploadButton = document.querySelector('.upload-button');");
                sb.append("  if (uploadButton) {");
                sb.append("    uploadButton.addEventListener('click', function () {");
                sb.append("      const files = fileInput.files;");
                sb.append("      if (!files.length) {");
                sb.append("        alert('Please select at least one file.');");
                sb.append("        return;");
                sb.append("      }");
                sb.append("      const formData = new FormData();");
                sb.append("for (let i = 0; i < files.length; i++) {\n" +
                        "    formData.append('files', files[i]);\n" +
                        "    formData.append('filenames', files[i].name);\n" +
                        "    formData.append('filesizes', files[i].size); \n" +
                        "    progressBar.style.width = '0';\n" +
                        "}\n");
                sb.append("      xhr = new XMLHttpRequest();");
                sb.append("      xhr.open('POST', '/', true);");
                sb.append("      xhr.upload.onprogress = function (e) {");
                sb.append("        const percentComplete = (e.loaded / e.total) * 100;");
                sb.append("        progressBar.style.width = percentComplete + '%';");
                sb.append("      };");
                sb.append("      xhr.onload = function () {");
                sb.append("        if (xhr.status === 200) {");
                sb.append("          uploadCompleteMessage.textContent = 'Upload complete.';");
                sb.append("          uploadButton.classList.add('upload-complete');");
                sb.append("          stopButtonContainer.style.display = 'none';");
                sb.append("          fileInput.value = null;");
                sb.append("          fileList.innerHTML = ''; ");
                sb.append("        } else {");
                sb.append("          alert('Upload failed. Please try again.');");
                sb.append("        }");
                sb.append("      };");
                sb.append("      xhr.send(formData);");
                sb.append("      uploadButton.classList.add('uploading');");
                sb.append("      stopButtonContainer.style.display = 'flex';");
                sb.append("    });");
                sb.append("  }");
                sb.append("  const stopButton = document.querySelector('.stop-button');");
                sb.append("  if (stopButton) {");
                sb.append("    stopButton.addEventListener('click', function () {");
                sb.append("      if (xhr) {");
                sb.append("        xhr.abort();");
                sb.append("        progressBar.style.width = '0';"); // Reset the progress bar width
                sb.append("        alert('Upload aborted.');");
                sb.append("      }");
                sb.append("    });");
                sb.append("  }");
                sb.append("  function stopUpload() {");
                sb.append("    if (xhr) {");
                sb.append("      xhr.abort();");
                sb.append("      progressBar.style.width = '0';"); // Reset the progress bar width
                sb.append("      alert('Upload aborted.');");
                sb.append("    }");
                sb.append("  }");
                sb.append("});");
                sb.append("function formatBytes(bytes, decimals = 2) {");
                sb.append("  if (bytes === 0) return '0 Bytes';");
                sb.append("  const k = 1024;");
                sb.append("  const dm = decimals < 0 ? 0 : decimals;");
                sb.append("  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];");
                sb.append("  const i = Math.floor(Math.log(bytes) / Math.log(k));");
                sb.append("  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];");
                sb.append("}");
                sb.append("</script>");



                sb.append("<div style=\"padding-left: 20px; text-align: center;  background-color: #266164; border-radius: 10px;\">");

                sb.append("<br><button onclick=\"history.back()\">Go Back</button><br>");
             sb.append("<br></div>");


                String[] fileList = requestedFile.list();
                if (fileList != null) {
                    List<String> directories = new ArrayList<>();
                    List<String> files = new ArrayList<>();

                    for (String fileName : fileList) {
                        File file = new File(requestedFile, fileName);
                        if (file.isDirectory()) {
                            directories.add(fileName);
                        } else {
                            files.add(fileName);
                        }
                    }

                    // Sort directories and files separately
                    Collections.sort(directories, String.CASE_INSENSITIVE_ORDER);
                    Collections.sort(files, String.CASE_INSENSITIVE_ORDER);


                    sb.append("</form>");

                    sb.append("<div style=\"padding: 20px; background-color: #e8ecec; border-radius: 10px;\">");

                    sb.append("<ul style=\"list-style: none; padding: 0;\">");
                    // Display directories first
                    for (String directory : directories) {
                        String href = uri + (uri.endsWith("/") ? "" : "/") + directory;
                        String icon = "<svg fill=\"#1c71d8\" height=\"20px\" width=\"20px\" version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 512 512\" xml:space=\"preserve\"><g id=\"SVGRepo_bgCarrier\" stroke-width=\"0\"></g><g id=\"SVGRepo_tracerCarrier\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></g><g id=\"SVGRepo_iconCarrier\"><g><g><path d=\"M496,108.132H273.456l-72.584-71.256H0v87.256v35.488v299.496c0,8.808,7.2,16.008,16,16.008h480c8.8,0,16-7.2,16-16 V124.132C512,115.332,504.8,108.132,496,108.132z M32,68.868h155.792l39.984,39.256H32V68.868z M480,443.124H32V159.62v-19.488 h448V443.124z\"></path></g></g></g></svg>";
                        sb.append("<li style=\"margin-bottom: 10px;\">" +
                                "<div style=\" padding: 10px; border-radius: 5px;\">" +
                                "<span style=\"margin-right: 10px;\">" + icon +
                                "<a href=\"" + href + "\" style=\"margin-left: 10px;\">" + directory + "</a></span></div></li>");
                    }


                    // Display files
                    for (String file : files) {
                        String href = uri + (uri.endsWith("/") ? "" : "/") + file;
                        String icon = "<svg width=\"20px\" height=\"20px\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"><g stroke-width=\"0\"/><g stroke-linecap=\"round\" stroke-linejoin=\"round\"/><path d=\"m13 3 .707-.707A1 1 0 0 0 13 2v1Zm6 6h1a1 1 0 0 0-.293-.707L19 9Zm-5.891-.546L14 8l-.891.454Zm.437.437L14 8l-.454.891ZM10 13a1 1 0 1 0-2 0h2Zm-2 3a1 1 0 1 0 2 0H8Zm.5-7a1 1 0 0 0 0 2V9Zm1 2a1 1 0 1 0 0-2v2Zm-1-5a1 1 0 0 0 0 2V6Zm1 2a1 1 0 1 0 0-2v2Zm8.408 12.782-.454-.891.454.891Zm.874-.874.891.454-.891-.454Zm-13.564 0-.891.454.891-.454Zm.874.874.454-.891-.454.891Zm0-17.564-.454-.891.454.891Zm-.874.874-.891-.454.891.454ZM12 3v4.4h2V3h-2Zm2.6 7H19V8h-4.4v2ZM12 7.4c0 .264 0 .521.017.738.019.229.063.499.201.77L14 8c.03.058.019.08.01-.025A8.205 8.205 0 0 1 14 7.4h-2Zm2.6.6c-.296 0-.459 0-.575-.01-.105-.009-.082-.02-.025.01l-.908 1.782c.271.138.541.182.77.201.217.018.474.017.738.017V8Zm-2.382.908a2 2 0 0 0 .874.874L14 8l-1.782.908ZM8 13v3h2v-3H8Zm.5-2h1V9h-1v2Zm0-3h1V6h-1v2ZM13 2H8.2v2H13V2ZM4 6.2v11.6h2V6.2H4ZM8.2 22h7.6v-2H8.2v2ZM20 17.8V9h-2v8.8h2Zm-.293-9.507-6-6-1.414 1.414 6 6 1.414-1.414ZM15.8 22c.544 0 1.011 0 1.395-.03.395-.033.789-.104 1.167-.297l-.908-1.782c-.05.025-.15.063-.422.085C16.75 20 16.377 20 15.8 20v2Zm2.2-4.2c0 .577 0 .949-.024 1.232-.022.272-.06.372-.085.422l1.782.908c.193-.378.264-.772.296-1.167.032-.384.031-.851.031-1.395h-2Zm.362 3.873a3 3 0 0 0 1.311-1.311l-1.782-.908a1 1 0 0 1-.437.437l.908 1.782ZM4 17.8c0 .544 0 1.011.03 1.395.033.395.104.789.297 1.167l1.782-.908c-.025-.05-.063-.15-.085-.422C6 18.75 6 18.377 6 17.8H4ZM8.2 20c-.577 0-.949 0-1.232-.024-.272-.022-.373-.06-.422-.085l-.908 1.782c.378.193.772.264 1.167.296.384.032.851.031 1.395.031v-2Zm-3.873.362a3 3 0 0 0 1.311 1.311l.908-1.782a1 1 0 0 1-.437-.437l-1.782.908ZM8.2 2c-.544 0-1.011 0-1.395.03-.395.033-.789.104-1.167.297l.908 1.782c.05-.025.15-.063.422-.085C7.25 4 7.623 4 8.2 4V2ZM6 6.2c0-.577 0-.949.024-1.232.022-.272.06-.373.085-.422l-1.782-.908c-.193.378-.264.772-.296 1.167C3.999 5.189 4 5.656 4 6.2h2Zm-.362-3.873a3 3 0 0 0-1.311 1.311l1.782.908a1 1 0 0 1 .437-.437l-.908-1.782Z\" fill=\"#1c71d8\"/></svg>";
                        sb.append("<li style=\"margin-bottom: 10px;\">" +
                                "<span style=\"font-size: 18px; margin-right: 10px;\">" + icon +
                                "<a href=\"" + href + "\" style=\"margin-left: 10px;\">" + file + "</a></span></li>");
                    }

                }
                sb.append("</div>");


                sb.append("</ul></div></body></html>");
                logHelp("Accessing Folder: "+uri);

                return newFixedLengthResponse(sb.toString());
            } else {
                // Serve file for download
                try {
                    FileInputStream fis = new FileInputStream(requestedFile);
                    String mime;
                    Response response = newFixedLengthResponse(Response.Status.OK, getcustomMimeTypeForFile(uri), fis, requestedFile.length());
                    response.addHeader("Content-Disposition", "attachment; filename=\"" + requestedFile.getName() + "\"");
                    logHelp("Downloading File: "+uri);
                    return response;
                } catch (IOException e) {
                    Log.e(TAG, "IOException while reading file: " + e.getMessage());
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error 500: " + e.getMessage());
                }
            }
        } else if (rootDir.isDirectory()) {
            // Serve directory listing page
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n<html><head>");
            sb.append("    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Wifi Files Browser</title>");
            sb.append("    <style>\n" +
                    "       {box-sizing: border-box;}body {margin: 0;padding: 0;font-family: \"Roboto\", sans-serif;font-size: 16px;line-height: 1.5;color: #333;background-color: #f5f5f5;}.container {max-width: 600px;margin: 0 auto;padding: 20px;}h2 {margin: 0;padding: 10px;font-size: 24px;font-weight: bold;color: #fff;background-color: #3f51b5;}form {display: inline;flex-direction: column;align-items: center;margin-top: 20px;}input[type=\"file\"] {margin: 10px 0;padding: 10px;border: 2px dashed #ccc;border-radius: 5px;cursor: pointer;}input[type=\"submit\"] {padding: 10px 30px;border: none;border-radius: 5px;background-color: #3f51b5;color: #fff;font-weight: bold;cursor: pointer;}button {margin-top: 10px;padding: 10px 20px;border: none;border-radius: 5px;background-color: #ccc;color: #333;font-weight: bold;cursor: pointer;}button:hover {background-color: #ddd;}ul {list-style: none;margin: 20px 0 0 0;padding: 0;}li {display: flex;justify-content: space-between;align-items: center;padding: 10px;border-bottom: 1px solid #ddd;}li:first-child {border-top: 1px solid #ddd;}a {text-decoration: none;color: #3f51b5;font-weight: bold;}a[dir] {color: #009688;}\n" +
                    "    </style>");
            sb.append("</head><body> <div class=\"container\">");
// File upload form

            sb.append("");


            sb.append("<style>body{font-family:Roboto,sans-serif;background-color:#f0f0f0;margin:20px}#file-selection{background-color:#fff;padding:20px;border-radius:8px;box-shadow:0 0 10px rgba(0,0,0,.1);display:flex;flex-direction:column;align-items:center}#file-input{display:none}#file-label{background-color:#3498db;color:#fff;padding:10px 20px;border-radius:5px;cursor:pointer;display:flex;align-items:center}#file-label:hover{background-color:#2980b9}#file-list{width:100%;border-collapse:collapse;margin-top:10px}table,th,td{border:1px solid #ddd}td,th{padding:15px;text-align:left}th{background-color:#f2f2f2}.remove-button{background-color:#e74c3c;color:#fff;border:none;padding:8px 15px;border-radius:4px;cursor:pointer}.remove-button:hover{background-color:#c0392b}.upload-button-container{display:flex;flex-direction:column;align-items:center}.upload-button{background-color:#2ecc71;color:#fff;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;margin-top:10px;pointer-events:none;opacity:0.5}.upload-button:hover{background-color:#27ae60}.uploading .upload-button,.upload-complete .upload-button{pointer-events:none;opacity:0.5}.uploading .upload-button:hover,.upload-complete .upload-button:hover{background-color:#2ecc71}.progress-bar-container{width:90%;display:flex;flex-direction:row;align-items:center;margin-top:10px}.progress-bar{flex:1;height:20px;background-color:#ddd;border-radius:4px;overflow:hidden;position:relative}.progress-bar-inner{height:100%;background-color:#4caf50;width:0;transition:width 0.3s ease-in-out}.stop-button-container{width:10%;margin-left:10px;display:flex;align-items:center;display:none}.stop-button{background-color:#e74c3c;color:#fff;border:none;padding:10px 10px;border-radius:5px;cursor:pointer}.stop-button:hover{background-color:#c0392b}.upload-complete-message{margin-top:10px;color:#2ecc71}h3{align-self:flex-start;margin-bottom:5px}</style>");
            sb.append("<div id=\"file-selection\" class=\"upload-button-container\">");
            sb.append("  <h3>Upload Files</h3>");
            sb.append("  <input type=\"file\" id=\"file-input\" name=\"files[]\" multiple>");
            sb.append("  <label id=\"file-label\" for=\"file-input\">");
            sb.append("    <span style=\"margin-right: 10px;\">+</span> Add Files.");
            sb.append("  </label>");
            sb.append("  <table id=\"file-list\">");
            sb.append("    <tr>");
            sb.append("      <th>File Name</th>");
            sb.append("      <th>Actions</th>");
            sb.append("    </tr>");
            sb.append("  </table>");
            sb.append("  <div class=\"progress-bar-container\">");
            sb.append("    <div class=\"progress-bar\">");
            sb.append("      <div class=\"progress-bar-inner\"></div>");
            sb.append("    </div>");
            sb.append("    <div class=\"stop-button-container\">");
            sb.append("      <button class=\"stop-button\" onclick=\"stopUpload()\">Stop</button>");
            sb.append("    </div>");
            sb.append("  </div>");
           // sb.append("  <button class=\"upload-button\" onclick=\"uploadFiles()\">Upload</button>");
            sb.append("  <button class=\"upload-button\" >Upload</button>");

            sb.append("  <div class=\"upload-complete-message\" id=\"upload-complete-message\"></div>");
            sb.append("</div>");
            sb.append("<script>");
            sb.append("document.addEventListener('DOMContentLoaded', function () {");
            sb.append("  const fileInput = document.getElementById('file-input');");
            sb.append("  const fileList = document.getElementById('file-list');");
            sb.append("  const progressBar = document.querySelector('.progress-bar-inner');");
            sb.append("  const stopButtonContainer = document.querySelector('.stop-button-container');");
            sb.append("  const uploadCompleteMessage = document.getElementById('upload-complete-message');");
            sb.append("  let xhr;");
            sb.append("  fileInput.addEventListener('change', function (event) {");
            sb.append("    const files = event.target.files;");
            sb.append("    const uploadButton = document.querySelector('.upload-button');");
            sb.append("    if (files.length > 0) {");
            sb.append("      uploadButton.style.pointerEvents = 'auto';");
            sb.append("      uploadButton.style.opacity = '1';");
            sb.append("    } else {");
            sb.append("      uploadButton.style.pointerEvents = 'none';");
            sb.append("      uploadButton.style.opacity = '0.5';");
            sb.append("    }");
            sb.append("    for (let i = 0; i < files.length; i++) {");
            sb.append("      const file = files[i];");
            sb.append("      const row = fileList.insertRow(-1);");
            sb.append("      const cell1 = row.insertCell(0);");
            sb.append("      cell1.textContent = file.name;");
            sb.append("      const fileSize = file.size;");
            sb.append("      cell1.textContent += ' (' + formatBytes(fileSize) + ')';");
            sb.append("      const cell2 = row.insertCell(1);");
            sb.append("      const removeButton = document.createElement('button');");
            sb.append("      removeButton.textContent = 'Remove';");
            sb.append("      removeButton.classList.add('remove-button');");
            sb.append("      removeButton.addEventListener('click', function () {");
            sb.append("        fileList.deleteRow(row.rowIndex);");
            sb.append("        fileInput.value = null;");
            sb.append("        const uploadButton = document.querySelector('.upload-button');");
            sb.append("        uploadButton.style.pointerEvents = 'none';");
            sb.append("        uploadButton.style.opacity = '0.5';");
            sb.append("      });");
            sb.append("      cell2.appendChild(removeButton);");
            sb.append("    }");
            sb.append("  });");
            sb.append("  const uploadButton = document.querySelector('.upload-button');");
            sb.append("  if (uploadButton) {");
            sb.append("    uploadButton.addEventListener('click', function () {");
            sb.append("      const files = fileInput.files;");
            sb.append("      if (!files.length) {");
            sb.append("        alert('Please select at least one file.');");
            sb.append("        return;");
            sb.append("      }");
            sb.append("      const formData = new FormData();");
            sb.append("for (let i = 0; i < files.length; i++) {\n" +
                    "    formData.append('files', files[i]);\n" +
                    "    formData.append('filenames', files[i].name);\n" +
                    "    formData.append('filesizes', files[i].size); \n" +
                    "    progressBar.style.width = '0';\n" +
                    "}\n");
            sb.append("      xhr = new XMLHttpRequest();");
            sb.append("      xhr.open('POST', '/', true);");
            sb.append("      xhr.upload.onprogress = function (e) {");
            sb.append("        const percentComplete = (e.loaded / e.total) * 100;");
            sb.append("        progressBar.style.width = percentComplete + '%';");
            sb.append("      };");
            sb.append("      xhr.onload = function () {");
            sb.append("        if (xhr.status === 200) {");
            sb.append("          uploadCompleteMessage.textContent = 'Upload complete.';");
            sb.append("          uploadButton.classList.add('upload-complete');");
            sb.append("          stopButtonContainer.style.display = 'none';");
            sb.append("          fileInput.value = null;");
            sb.append("          fileList.innerHTML = ''; ");
            sb.append("        } else {");
            sb.append("          alert('Upload failed. Please try again.');");
            sb.append("        }");
            sb.append("      };");
            sb.append("      xhr.send(formData);");
            sb.append("      uploadButton.classList.add('uploading');");
            sb.append("      stopButtonContainer.style.display = 'flex';");
            sb.append("    });");
            sb.append("  }");
            sb.append("  const stopButton = document.querySelector('.stop-button');");
            sb.append("  if (stopButton) {");
            sb.append("    stopButton.addEventListener('click', function () {");
            sb.append("      if (xhr) {");
            sb.append("        xhr.abort();");
            sb.append("        progressBar.style.width = '0';"); // Reset the progress bar width
            sb.append("        alert('Upload aborted.');");
            sb.append("      }");
            sb.append("    });");
            sb.append("  }");
            sb.append("  function stopUpload() {");
            sb.append("    if (xhr) {");
            sb.append("      xhr.abort();");
            sb.append("      progressBar.style.width = '0';"); // Reset the progress bar width
            sb.append("      alert('Upload aborted.');");
            sb.append("    }");
            sb.append("  }");
            sb.append("});");
            sb.append("function formatBytes(bytes, decimals = 2) {");
            sb.append("  if (bytes === 0) return '0 Bytes';");
            sb.append("  const k = 1024;");
            sb.append("  const dm = decimals < 0 ? 0 : decimals;");
            sb.append("  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];");
            sb.append("  const i = Math.floor(Math.log(bytes) / Math.log(k));");
            sb.append("  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];");
            sb.append("}");
            sb.append("</script>");





            String[] fileList = rootDir.list();
            if (fileList != null) {
                List<String> directories = new ArrayList<>();
                List<String> files = new ArrayList<>();

                for (String fileName : fileList) {
                    File file = new File(rootDir, fileName);
                    if (file.isDirectory()) {
                        directories.add(fileName);
                    } else {
                        files.add(fileName);
                    }
                }

                // Sort directories and files separately
                Collections.sort(directories, String.CASE_INSENSITIVE_ORDER);
                Collections.sort(files, String.CASE_INSENSITIVE_ORDER);


                sb.append("</form>");

                sb.append("<div style=\"padding: 20px; background-color: #e8ecec; border-radius: 10px;\">");

                sb.append("<ul style=\"list-style: none; padding: 0;\">");
                // Display directories first
                for (String directory : directories) {
                    String href = (uri.endsWith("/") ? "" : "/") + directory;

                    String icon = "<svg fill=\"#1c71d8\" height=\"20px\" width=\"20px\" version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 512 512\" xml:space=\"preserve\"><g id=\"SVGRepo_bgCarrier\" stroke-width=\"0\"></g><g id=\"SVGRepo_tracerCarrier\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></g><g id=\"SVGRepo_iconCarrier\"><g><g><path d=\"M496,108.132H273.456l-72.584-71.256H0v87.256v35.488v299.496c0,8.808,7.2,16.008,16,16.008h480c8.8,0,16-7.2,16-16 V124.132C512,115.332,504.8,108.132,496,108.132z M32,68.868h155.792l39.984,39.256H32V68.868z M480,443.124H32V159.62v-19.488 h448V443.124z\"></path></g></g></g></svg>";
                    sb.append("<li style=\"margin-bottom: 10px;\">" +
                            "<div style=\" padding: 10px; border-radius: 5px;\">" +
                            "<span style=\"margin-right: 10px;\">" + icon +
                                    "<a style =\"margin-left :10px;\" href=\"" + href + "\">" + directory + "</a></li>");



                 //   sb.append("<li><a href=\"" + href + "\">" + directory + "</a> [Folder]</li>");
                }

                // Display files
                for (String file : files) {
                    String href = (uri.endsWith("/") ? "" : "/") + file;
                    String icon = "<svg width=\"20px\" height=\"20px\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"><g stroke-width=\"0\"/><g stroke-linecap=\"round\" stroke-linejoin=\"round\"/><path d=\"m13 3 .707-.707A1 1 0 0 0 13 2v1Zm6 6h1a1 1 0 0 0-.293-.707L19 9Zm-5.891-.546L14 8l-.891.454Zm.437.437L14 8l-.454.891ZM10 13a1 1 0 1 0-2 0h2Zm-2 3a1 1 0 1 0 2 0H8Zm.5-7a1 1 0 0 0 0 2V9Zm1 2a1 1 0 1 0 0-2v2Zm-1-5a1 1 0 0 0 0 2V6Zm1 2a1 1 0 1 0 0-2v2Zm8.408 12.782-.454-.891.454.891Zm.874-.874.891.454-.891-.454Zm-13.564 0-.891.454.891-.454Zm.874.874.454-.891-.454.891Zm0-17.564-.454-.891.454.891Zm-.874.874-.891-.454.891.454ZM12 3v4.4h2V3h-2Zm2.6 7H19V8h-4.4v2ZM12 7.4c0 .264 0 .521.017.738.019.229.063.499.201.77L14 8c.03.058.019.08.01-.025A8.205 8.205 0 0 1 14 7.4h-2Zm2.6.6c-.296 0-.459 0-.575-.01-.105-.009-.082-.02-.025.01l-.908 1.782c.271.138.541.182.77.201.217.018.474.017.738.017V8Zm-2.382.908a2 2 0 0 0 .874.874L14 8l-1.782.908ZM8 13v3h2v-3H8Zm.5-2h1V9h-1v2Zm0-3h1V6h-1v2ZM13 2H8.2v2H13V2ZM4 6.2v11.6h2V6.2H4ZM8.2 22h7.6v-2H8.2v2ZM20 17.8V9h-2v8.8h2Zm-.293-9.507-6-6-1.414 1.414 6 6 1.414-1.414ZM15.8 22c.544 0 1.011 0 1.395-.03.395-.033.789-.104 1.167-.297l-.908-1.782c-.05.025-.15.063-.422.085C16.75 20 16.377 20 15.8 20v2Zm2.2-4.2c0 .577 0 .949-.024 1.232-.022.272-.06.372-.085.422l1.782.908c.193-.378.264-.772.296-1.167.032-.384.031-.851.031-1.395h-2Zm.362 3.873a3 3 0 0 0 1.311-1.311l-1.782-.908a1 1 0 0 1-.437.437l.908 1.782ZM4 17.8c0 .544 0 1.011.03 1.395.033.395.104.789.297 1.167l1.782-.908c-.025-.05-.063-.15-.085-.422C6 18.75 6 18.377 6 17.8H4ZM8.2 20c-.577 0-.949 0-1.232-.024-.272-.022-.373-.06-.422-.085l-.908 1.782c.378.193.772.264 1.167.296.384.032.851.031 1.395.031v-2Zm-3.873.362a3 3 0 0 0 1.311 1.311l.908-1.782a1 1 0 0 1-.437-.437l-1.782.908ZM8.2 2c-.544 0-1.011 0-1.395.03-.395.033-.789.104-1.167.297l.908 1.782c.05-.025.15-.063.422-.085C7.25 4 7.623 4 8.2 4V2ZM6 6.2c0-.577 0-.949.024-1.232.022-.272.06-.373.085-.422l-1.782-.908c-.193.378-.264.772-.296 1.167C3.999 5.189 4 5.656 4 6.2h2Zm-.362-3.873a3 3 0 0 0-1.311 1.311l1.782.908a1 1 0 0 1 .437-.437l-.908-1.782Z\" fill=\"#1c71d8\"/></svg>";
                    sb.append("<li style=\"margin-bottom: 10px;\">" +
                            "<span style=\"font-size: 18px; margin-right: 10px;\">" + icon +
                            "<a href=\"" + href + "\">" + file + "</a></li>");

                }

                sb.append("</ul></div></body></html>");
            }


            sb.append("</ul></div></body></html>");

            return newFixedLengthResponse(sb.toString());
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Error 404: File not found");
        }
    }


    private String getcustomMimeTypeForFile(String uri) {
        int dot = uri.lastIndexOf('.');
        String extension = uri.substring(dot + 1);
        String mime = MIME_TYPES.get(extension.toLowerCase());
        if (mime == null) {
            mime = "application/octet-stream";
        }
        return mime;
    }
    private String getUniqueFilename(File targetDirectory, String filename) {
        File destFile = new File(targetDirectory, filename);

        // Check if a file with the same name already exists in the target directory
        if (destFile.exists()) {
            // If it does, append a number to the filename to make it unique
            int count = 1;
            int lastDotIndex = filename.lastIndexOf(".");
            String nameWithoutExtension = lastDotIndex >= 0 ? filename.substring(0, lastDotIndex) : filename;
            String extension = lastDotIndex >= 0 ? filename.substring(lastDotIndex) : "";
            destFile = new File(targetDirectory, nameWithoutExtension + "_" + count + extension);

            // Keep incrementing the count until a unique filename is found
            while (destFile.exists()) {
                count++;
                destFile = new File(targetDirectory, nameWithoutExtension + "_" + count + extension);
            }
        }

        // Return the filename, either the original one or the modified one
        return destFile.getName();
    }



    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("avi", "video/x-msvideo");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("csv", "text/csv");
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("ics", "text/calendar");
        MIME_TYPES.put("jar", "application/java-archive");
        MIME_TYPES.put("java", "text/x-java-source");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("mpeg", "video/mpeg");
    }}