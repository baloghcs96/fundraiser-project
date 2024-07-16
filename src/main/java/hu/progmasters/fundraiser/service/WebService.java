package hu.progmasters.fundraiser.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class WebService {

    private SpringTemplateEngine templateEngine;

    public ResponseEntity<String> createHtmlContent(String htmlContentPath, String pageTitle) {
        Context context = new Context();
        context.setVariable("title", pageTitle);
        String processedHtml;
        try {
            // A Thymeleaf TemplateEngine használata a HTML tartalom feldolgozására
            processedHtml = templateEngine.process(htmlContentPath, context);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing HTML page");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(processedHtml, headers, HttpStatus.OK);
    }

    private String getHeadContent(String title) {
        return "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>" + title + "</title>" +
                "<link crossorigin=\"anonymous\" " +
                "href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" " +
                "integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\" " +
                "rel=\"stylesheet\">" +
                "<link href=\"/css/base.css\" rel=\"stylesheet\">" +
                "<script crossorigin=\"anonymous\" " +
                "integrity=\"sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r\" " +
                "src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js\"></script>" +
                "<script crossorigin=\"anonymous\" " +
                "integrity=\"sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy\" " +
                "src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js\"></script>" +
                "</head>";
    }
}
