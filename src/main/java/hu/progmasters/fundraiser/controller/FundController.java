package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.dto.incoming.FundSaveCommand;
import hu.progmasters.fundraiser.dto.incoming.FundUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FundInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundUpdateInfo;
import hu.progmasters.fundraiser.exception.FundNotFoundByCategoryException;
import hu.progmasters.fundraiser.exception.FundNotFoundByTitleException;
import hu.progmasters.fundraiser.service.FundService;
import hu.progmasters.fundraiser.service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/fundraiser/fund")
@AllArgsConstructor
@Slf4j
public class FundController {

    private final FundService fundService;
    private final SecurityService securityService;

    @PostMapping()
    @ResponseStatus(CREATED)
    public FundInfo saveFund(@Valid @ModelAttribute FundSaveCommand command) {
        log.info("Fund creation requested, POST /api/fundraiser/account: {}", command.toString());
        Authentication authentication = securityService.getAuthentication();
        return fundService.saveFund(command, authentication);
    }

    @PutMapping("/update/{fundId}")
    @ResponseStatus(OK)
    public FundUpdateInfo updateFund(@PathVariable("fundId") Long fundId, @Valid @ModelAttribute FundUpdateCommand command) {
        log.info("Fund update requested, PUT /api/fundraiser/fund/{}", fundId);
        return fundService.updateFund(fundId, command);
    }

    @GetMapping("/{fundId}")
    @ResponseStatus(OK)
    public FundInfo getFundById(@PathVariable("fundId") Long fundId) {
        return fundService.getFundInfoById(fundId);
    }

    @GetMapping("/category")
    @ResponseStatus(OK)
    public List<FundInfo> findAllByCategory(@RequestParam(value = "category", required = false) Category category) throws FundNotFoundByCategoryException {
        log.info("Fund list requested by category, GET /api/fundraiser/fund");
        return fundService.findAllByCategory(category);
    }

    @GetMapping("/title")
    @ResponseStatus(OK)
    public List<FundInfo> findAllByTitle(@RequestParam(value = "title", required = false) String title) throws FundNotFoundByTitleException {
        log.info("Fund list requested by title, GET /api/fundraiser/fund");
        return fundService.findAllByTitle(title);
    }

}
