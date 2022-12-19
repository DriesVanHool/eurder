package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateItemGroupDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.services.OrderService;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "orders")
public class OrderController {
    OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public OrderDto createItem(@Valid @RequestBody List<CreateItemGroupDto> createItemGroupDtos, @RequestHeader String authorization) throws ParseException {
        return orderService.placeOrder(createItemGroupDtos, authorization);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public TotalOrderReportDto getOrderReport(@RequestHeader String authorization) throws ParseException{
        return orderService.getOrderReport(authorization);
    }

/*    @PostMapping(path = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto reorderOrder(@RequestHeader String authorization, @PathVariable String orderId) {
        return orderService.reorderOrder(orderId, securityService.getUserId(authorization));
    }*/
}
