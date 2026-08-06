package io.github.itzispyder.improperui.util.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class ImproperUIRenderPipelines {
    
    public static final ColorTargetState WITH_BLEND = new ColorTargetState(BlendFunction.TRANSLUCENT);
    public static final DepthStencilState DEPTH_NONE = new DepthStencilState(CompareOp.ALWAYS_PASS, false);

    public static final RenderPipeline PIPELINE_QUADS = RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation("pipeline/global_fill_pipeline")
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(WITH_BLEND)
            .withCull(false)
            .withDepthStencilState(DEPTH_NONE)
            .build();

    public static final RenderPipeline PIPELINE_TEX_QUADS = RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation("pipeline/gui_textured")
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(WITH_BLEND)
            .withCull(false)
            .withDepthStencilState(DEPTH_NONE)
            .build();
}