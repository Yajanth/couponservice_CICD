package com.tus.coupon.unit;

import com.tus.coupon.controller.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tus.coupon.model.Coupon;
import com.tus.coupon.repo.CouponRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public class CouponRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CouponRepo couponRepo;

    @InjectMocks
    private CouponRestController couponRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponRestController).build();
    }

    @Test
    public void testCreateCoupon() throws Exception {
        Coupon coupon = new Coupon(1L, "DISCOUNT10", new BigDecimal("10.00"), "2025-12-31");
        when(couponRepo.save(any(Coupon.class))).thenReturn(coupon);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/couponapi/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"DISCOUNT10\",\"discount\":10.00,\"expDate\":\"2025-12-31\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DISCOUNT10"))
                .andExpect(jsonPath("$.discount").value(10.00))
                .andExpect(jsonPath("$.expDate").value("2025-12-31"));
    }

    @Test
    public void testGetCouponByCode() throws Exception {
        Coupon coupon = new Coupon(1L, "DISCOUNT10", new BigDecimal("10.00"), "2025-12-31");
        when(couponRepo.findByCode("DISCOUNT10")).thenReturn(coupon);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/couponapi/coupons/DISCOUNT10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DISCOUNT10"))
                .andExpect(jsonPath("$.discount").value(10.00))
                .andExpect(jsonPath("$.expDate").value("2025-12-31"));
    }

    @Test
    public void testGetAllCoupons() throws Exception {
        Coupon coupon = new Coupon(1L, "DISCOUNT10", new BigDecimal("10.00"), "2025-12-31");
        when(couponRepo.findAll()).thenReturn(Collections.singletonList(coupon));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/couponapi/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("DISCOUNT10"))
                .andExpect(jsonPath("$[0].discount").value(10.00))
                .andExpect(jsonPath("$[0].expDate").value("2025-12-31"));
    }
}
