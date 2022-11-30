package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateItemGroupDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.services.OrderService;
import com.switchfully.eurder.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "orders")
public class OrderController {
    SecurityService securityService;
    OrderService orderService;

    public OrderController(SecurityService securityService, OrderService orderService) {
        this.securityService = securityService;
        this.orderService = orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createItem(@RequestHeader String authorization, @RequestBody List<CreateItemGroupDto> createItemGroupDtos) {
        securityService.validateAuthorisation(authorization, Feature.PLACE_ORDER);
        return orderService.placeOrder(createItemGroupDtos, securityService.getUserId(authorization));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public TotalOrderReportDto getOrderReport(@RequestHeader String authorization) {
        securityService.validateAuthorisation(authorization, Feature.GET_ORDER_REPORT);
        return orderService.getOrderReport(securityService.getUserId(authorization));
    }

    @PostMapping(path = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto reorderOrder(@RequestHeader String authorization, @PathVariable String orderId) {
        securityService.validateAuthorisation(authorization, Feature.PLACE_ORDER);
        return orderService.reorderOrder(orderId, securityService.getUserId(authorization));
    }
}
