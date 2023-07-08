package com.rusefi.candlelight;


public interface Protocol {

    /**
     * typedef struct {
     * uint32_t feature;
     * uint32_t fclk_can;
     * uint32_t tseg1_min;
     * uint32_t tseg1_max;
     * uint32_t tseg2_min;
     * uint32_t tseg2_max;
     * uint32_t sjw_max;
     * uint32_t brp_min;
     * uint32_t brp_max;
     * uint32_t brp_inc;
     * } candle_capability_t;
     */
    int candle_capability_t_size = 40;
}
