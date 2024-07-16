package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundUpdateInfo {

    private String title;

    private String description;

    private List<String> images;


}
