package excelupload.ExcelUploadApp.endpoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.util.Base64;

@Path("file")
public class UploadFileService {

    private final String UPLOADED_FILE_PATH = "d:\\";
    
    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) throws URISyntaxException {
        String fileName = "";
        
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");

        for (InputPart inputPart : inputParts) {
   
         try {

            MultivaluedMap<String, String> header = inputPart.getHeaders();
            fileName = getFileName(header);

            System.out.println("media type ---->"+inputPart.getMediaType());
            
            //convert the uploaded file to inputstream
            InputStream is = inputPart.getBody(InputStream.class,null);
            InputStream is2 = inputPart.getBody(InputStream.class,null);
            InputStreamReader ir = new InputStreamReader(is2);
            System.out.println("input stream encoding--->"+ir.getEncoding());
            System.out.println("Default Charset ------>"+Charset.defaultCharset());
            BufferedReader r = new BufferedReader(ir);

            
            byte [] bytes = IOUtils.toByteArray(is);
            System.out.println("file byte []---"+bytes);
            //constructs upload file path
            fileName = UPLOADED_FILE_PATH + fileName;
                
            writeFile(bytes,fileName);
            
            fileName = UPLOADED_FILE_PATH + "5_Test.xlsx";
//            File f = new File(fileName);
//            while (r.readLine() != null) {
//            	FileUtils.writeByteArrayToFile(f, r.readLine().getBytes(Charset.forName("UTF-8")));
//            }
            
            java.nio.file.Path dst = Paths.get(fileName);
            BufferedWriter bw = Files.newBufferedWriter(dst, Charset.forName(ir.getEncoding()));
            PrintWriter printWriter = new PrintWriter(bw);
            IOUtils.copy(r, printWriter);
            printWriter.close();
            String line;
//            while ((line = r.readLine()) != null) {
//            	//bw.write(line);
//                // must do this: .readLine() will have stripped line endings
//            	//bw.newLine();
//            }
//            bw.close();
            
            fileName = UPLOADED_FILE_PATH + "2_Test.xlsx";
            FileUtils.writeByteArrayToFile(new File(fileName), bytes);
            String s = Base64.encodeBytes(bytes);
            
            byte[] decodeBytes = Base64.decode(s);
            fileName = UPLOADED_FILE_PATH + "3_Test.xlsx";
            FileUtils.writeByteArrayToFile(new File(fileName), decodeBytes);
            System.out.println("Done");

//            Workbook workbook = WorkbookFactory.create(new File(fileName));
//            fileName = UPLOADED_FILE_PATH + "4_Test.xls";
//        	FileOutputStream outputStream = new FileOutputStream(fileName);
//            workbook.write(outputStream);
//            outputStream.close();
//            workbook.close();
          } catch (IOException e) {
            e.printStackTrace();
          }

        }

        return Response.status(200)
            .entity("uploadFile is called, Uploaded file name : " + fileName).build();

    }

    /**
     * header sample
     * {
     * 	Content-Type=[image/png], 
     * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    //get uploaded filename, is there a easy way in RESTEasy?
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");
                
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    //save to somewhere
    private void writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();

    }
    
    @POST
    @Path("/upload2")
    @Consumes("multipart/form-data")
    public Response uploadFile(@MultipartForm FileUploadForm form) {

        String fileName = "d:\\anything.xlsx";
        
        try {
            writeFile(form.getData(), fileName);
        } catch (IOException e) {
            
            e.printStackTrace();
        }

        System.out.println("Done");

        return Response.status(200)
            .entity("uploadFile is called, Uploaded file name : " + fileName).build();

    }

 }
