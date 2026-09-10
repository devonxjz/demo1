package dev.controllers;

import dev.dtos.MediaFileInfo;
import dev.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CookieUtil.getSyncedUser(request);
        File downloadDir = resolveDownloadDir();

        String fileName = request.getParameter("file");
        if (fileName != null && !fileName.trim().isEmpty()) {
            File file = new File(downloadDir, fileName.trim());
            try {
                String canonicalDirPath = downloadDir.getCanonicalPath();
                String canonicalFilePath = file.getCanonicalPath();

                if (canonicalFilePath.startsWith(canonicalDirPath) && file.exists() && file.isFile()) {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                    response.setContentLengthLong(file.length());
                    try (InputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
                        in.transferTo(out);
                    }
                    return;
                }
            } catch (IOException ignored) {}
        }

        List<MediaFileInfo> files = new ArrayList<>();
        File[] fileArray = downloadDir.listFiles();
        if (fileArray != null) {
            for (File f : fileArray) {
                if (f.isFile()) {
                    long size = f.length();
                    String sizeFormatted = size < 1024 ? size + " B" : (size / 1024) + " KB";
                    files.add(new MediaFileInfo(f.getName(), sizeFormatted));
                }
            }
        }
        files.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));

        request.setAttribute("fileList", files);
        request.getRequestDispatcher("/WEB-INF/views/download.jsp").forward(request, response);
    }

    private File resolveDownloadDir() {
        String realPath = getServletContext().getRealPath("/download");
        if (realPath != null) {
            File dir = new File(realPath);
            if (dir.exists()) return dir;
        }
        return new File("src/main/webapp/download");
    }
}
