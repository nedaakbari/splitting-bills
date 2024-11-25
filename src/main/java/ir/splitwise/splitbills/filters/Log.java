package ir.splitwise.splitbills.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;

@Component
public class Log extends OncePerRequestFilter {
/*    @Value("${logger.file.path}")
    private String path;*/

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        Enumeration<String> headerNames = request.getHeaderNames();//???
//todo request body
        filterChain.doFilter(request, response);
        Collection<String> responseHeaderNames = response.getHeaderNames();
//todo response body
        String log = "requestURI: " + requestURI + " ,method: " + method + ", requestHeaderNames: " + headerNames + " , responseHeaderNames:" + responseHeaderNames;
        writeToFile(log);
    }

    private void writeToFile(String body) throws IOException {//    //todo log.json to kafka or files
        var directoryPath = Paths.get("").toAbsolutePath() + "/log";
        var logFile = Path.of(directoryPath);
        if (!Files.exists(logFile.getParent())) {
            Files.createDirectories(logFile.getParent());
        }
        Files.writeString(logFile, body);
    }
}
