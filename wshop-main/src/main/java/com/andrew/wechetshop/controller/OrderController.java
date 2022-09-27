package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.OrderInfo;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.entity.OrderResponse;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.service.OrderService;
import com.andrew.wechetshop.service.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public Response<OrderResponse> createOrder(@RequestBody OrderInfo orderInfo) {
        orderService.deductStock(orderInfo);
        return Response.of(orderService.createOrder(orderInfo, UserContext.getCurrentUser().getId()));
    }

    @DeleteMapping("/order/{id}")
    public Response<OrderResponse> deleteOrder(@PathVariable long id) {
        return Response.of(orderService.deleteOrder(id, UserContext.getCurrentUser().getId()));
    }

    @GetMapping("/order")
    public PagedResponse<OrderResponse> getOrder(int pageNum, int pageSize, String status) {
        try {
            DataStatus dataStatus = DataStatus.fromName(status);
            return orderService.getOrder(pageNum, pageSize, dataStatus, UserContext.getCurrentUser().getId());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw HttpException.badRequest("订单状态错误");
        }
    }

    @RequestMapping(value = "/order/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public Response<OrderResponse> updateOrder(@PathVariable("id") long id, @RequestBody Order order) {
        Long userId = UserContext.getCurrentUser().getId();
        order.setId(id);
        if (order.getStatus() != null) {
            return Response.of(orderService.updateOrderStatus(order, userId));
        } else {
            return Response.of(orderService.updateExpressInformation(order, userId));
        }
    }

}
