package hu.progmasters.fundraiser.dto.incoming;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.validation.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundSaveCommand {


    @NotBlank(message = "Title must not be blank")
    private String title;

    @EnumValidator(enumClass = Category.class)
    private String category;

    @EnumValidator(enumClass = Currency.class)
    private String currency;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @Positive(message = "Goal amount must be positive")
    private Double goalAmount;

    private List<MultipartFile> images;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closeDate;

}
