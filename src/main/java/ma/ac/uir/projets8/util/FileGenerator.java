package ma.ac.uir.projets8.util;

import jakarta.servlet.http.HttpServletResponse;
import ma.ac.uir.projets8.model.Student;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class FileGenerator {


    private static final String path = "C:/Users/yassi/Bureau/";
    public static void generateStudentFile(HttpServletResponse response, List<Student> members, String fileName) {

        String fullPath = path+fileName;
        try (OutputStream os = new FileOutputStream(fullPath)) {

            Workbook wb = new Workbook(os, "MyApplication", "1.0");
            Worksheet ws = wb.newWorksheet("sheet 1");
            ws.range(0, 0, members.size(), 3).createTable()
                    .setName("test")
                    .setDisplayName("display name")
                    .styleInfo();
            ws.value(0, 0, "Student id");
            ws.value(0, 1, "first name");
            ws.value(0, 2, "last name");
            ws.value(0, 3, "email");
            ws.value(0, 4, "major");
            ws.value(0, 5, "level");
            for (int i = 0; i < members.size(); i++) {
                ws.value(i + 1, 0, members.get(i).getFirstName());
                ws.value(i + 1, 1, members.get(i).getLastName());
                ws.value(i + 1, 2, members.get(i).getEmail());
                ws.value(i + 1, 3, members.get(i).getStudentId());
                ws.value(i + 1, 4, members.get(i).getMajor());
                ws.value(i + 1, 5, members.get(i).getLevel());
            }
            ws.finish();
            wb.finish();


            File file = new File(fullPath);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + file.getName());

            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);

            // copy from in to out
            IOUtils.copy(in, out);

            out.close();
            in.close();
            os.close();
            file.delete();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }}
