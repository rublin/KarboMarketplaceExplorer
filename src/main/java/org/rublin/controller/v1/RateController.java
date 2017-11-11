package org.rublin.controller.v1;

import org.rublin.dto.RateResponseDto;
import org.rublin.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = RateController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RateController extends AbstractController {

    static final String URL = URL_PREFIX;

    @Autowired
    private RateService rateService;

    @RequestMapping(value = "/rate", method = RequestMethod.GET)
    public RateResponseDto rate() {
        return rateService.getCurrentRate();
    }
}
