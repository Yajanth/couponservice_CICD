package com.tus.coupon.unit;

import static org.mockito.Mockito.*;

import com.tus.coupon.CouponserviceApplication;
import com.tus.coupon.controller.CouponRestController;
import com.tus.coupon.model.Coupon;
import com.tus.coupon.repo.CouponRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class CouponRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponRepo repo;

    @InjectMocks
    private CouponRestController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreateCoupon() throws Exception {
        // Creating a sample coupon
        Coupon coupon = new Coupon(null, "SAVE10", new BigDecimal("10.00"), "2025-12-31");
        Coupon savedCoupon = new Coupon(1L, "SAVE10", new BigDecimal("10.00"), "2025-12-31");

        // Mocking the repo's save method
        when(repo.save(any(Coupon.class))).thenReturn(savedCoupon);

        mockMvc.perform(post("/couponapi/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coupon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discount").value(10.00))
                .andExpect(jsonPath("$.expDate").value("2025-12-31"));
    }
    @Test
    void contextLoads() {
   }

    @Test
    void testGetCouponByCouponCode() throws Exception {
        // Creating a sample coupon
        Coupon coupon = new Coupon(1L, "SAVE10", new BigDecimal("10.00"), "2025-12-31");

        // Mocking the repo's findByCode method
        when(repo.findByCode("SAVE10")).thenReturn(coupon);

        mockMvc.perform(get("/couponapi/coupons/SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discount").value(10.00))
                .andExpect(jsonPath("$.expDate").value("2025-12-31"));
    }

    @Test
    void testGetAllCoupons() throws Exception {
        // Creating a list of coupons
        List<Coupon> coupons = Arrays.asList(
                new Coupon(1L, "SAVE10", new BigDecimal("10.00"), "2025-12-31"),
                new Coupon(2L, "SAVE20", new BigDecimal("20.00"), "2025-12-31")
        );

        // Mocking the repo's findAll method
        when(repo.findAll()).thenReturn(coupons);

        mockMvc.perform(get("/couponapi/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("SAVE20"));
    }
}