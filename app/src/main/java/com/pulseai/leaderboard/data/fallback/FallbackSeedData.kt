package com.pulseai.leaderboard.data.fallback

import com.pulseai.leaderboard.data.remote.OpenRouterArchitectureDto
import com.pulseai.leaderboard.data.remote.OpenRouterModelDto
import com.pulseai.leaderboard.data.remote.OpenRouterPricingDto
import com.pulseai.leaderboard.data.remote.OpenRouterTopProviderDto

object FallbackSeedData {

    val seedModels: List<OpenRouterModelDto> = listOf(
        OpenRouterModelDto(
            id = "anthropic/claude-opus-5.5",
            name = "Claude Opus 5.5",
            created = 1735689600L,
            description = "Anthropic's apex frontier intelligence model for complex multi-step reasoning, autonomous agentic workflows, and deep synthesis.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.000015", completion = "0.000075"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 32768, isModerated = true),
            supportedParameters = listOf("tools", "reasoning", "temperature", "max_tokens")
        ),
        OpenRouterModelDto(
            id = "openai/gpt-6-astra",
            name = "GPT-6 Astra",
            created = 1735776000L,
            description = "OpenAI's flagship multi-modal reasoning foundation model with native system-2 reflection and exceptional code architecture design.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000125", completion = "0.000050"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "audio"),
                outputModalities = listOf("text", "audio")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = true),
            supportedParameters = listOf("tools", "reasoning", "json_schema")
        ),
        OpenRouterModelDto(
            id = "anthropic/claude-fable-5.1",
            name = "Claude Fable 5.1",
            created = 1735516800L,
            description = "Ultra high-speed hybrid frontier model specialized in agentic tool utilization, coding execution, and ultra-long context reasoning.",
            contextLength = 500_000,
            pricing = OpenRouterPricingDto(prompt = "0.000006", completion = "0.000024"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = true),
            supportedParameters = listOf("tools", "reasoning")
        ),
        OpenRouterModelDto(
            id = "openai/gpt-6.1-sol",
            name = "GPT-6.1 Sol",
            created = 1735862400L,
            description = "High-efficiency frontier model optimized for real-time complex reasoning tasks, deep codebase refactoring, and agent tool execution.",
            contextLength = 256_000,
            pricing = OpenRouterPricingDto(prompt = "0.000008", completion = "0.000032"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = true),
            supportedParameters = listOf("tools", "reasoning")
        ),
        OpenRouterModelDto(
            id = "google/gemini-4-argon",
            name = "Gemini 4 Argon",
            created = 1735430400L,
            description = "Google DeepMind's state-of-the-art multimodal model with 2M token context window, deep cross-modal reasoning, and near-zero latency.",
            contextLength = 2_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.000005", completion = "0.000020"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "video", "audio"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 65536, isModerated = true),
            supportedParameters = listOf("tools", "json_mode", "reasoning")
        ),
        OpenRouterModelDto(
            id = "deepseek/deepseek-v4.1-flash",
            name = "DeepSeek V4.1 Flash",
            created = 1735948800L,
            description = "Next-gen open-architecture Mixture-of-Experts with revolutionary value-per-token ratio, lightning 145 tps throughput, and stellar math skills.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000027", completion = "0.0000011"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools", "temperature")
        ),
        OpenRouterModelDto(
            id = "deepseek/deepseek-r1",
            name = "DeepSeek R1",
            created = 1737331200L,
            description = "Open reasoning model utilizing large-scale reinforcement learning for step-by-step mathematical reasoning, STEM, and algorithmic coding.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000055", completion = "0.00000219"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 32768, isModerated = false),
            supportedParameters = listOf("reasoning", "tools")
        ),
        OpenRouterModelDto(
            id = "meta-llama/llama-4-maverick",
            name = "Llama 4 Maverick (405B)",
            created = 1736121600L,
            description = "Meta's flagship open-weights giant with native synthetic data mastery, multi-lingual fluency, and enterprise-grade code generation.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000018", completion = "0.0000018"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools", "temperature")
        ),
        OpenRouterModelDto(
            id = "qwen/qwen-3-max",
            name = "Qwen 3 Max",
            created = 1736035200L,
            description = "Alibaba's most powerful enterprise foundation model with peak performance on STEM benchmarks, multilingual translation, and reasoning.",
            contextLength = 256_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000016", completion = "0.0000064"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = false),
            supportedParameters = listOf("tools", "reasoning")
        ),
        OpenRouterModelDto(
            id = "x-ai/grok-3",
            name = "Grok 3",
            created = 1736208000L,
            description = "xAI's colossal compute model trained on massive real-time datasets with witty personality, truth-seeking reasoning, and high throughput.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.000005", completion = "0.000015"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools", "reasoning")
        ),
        OpenRouterModelDto(
            id = "openai/o1",
            name = "OpenAI o1",
            created = 1726099200L,
            description = "System-2 reasoning model that produces internal thought chains before answering difficult math, science, and coding problems.",
            contextLength = 200_000,
            pricing = OpenRouterPricingDto(prompt = "0.000015", completion = "0.000060"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 100000, isModerated = true),
            supportedParameters = listOf("reasoning")
        ),
        OpenRouterModelDto(
            id = "anthropic/claude-3.5-sonnet",
            name = "Claude 3.5 Sonnet",
            created = 1718841600L,
            description = "Industry standard benchmark for coding, agentic tool workflows, visual diagram interpretation, and natural dialogue nuance.",
            contextLength = 200_000,
            pricing = OpenRouterPricingDto(prompt = "0.000003", completion = "0.000015"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = true),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "openai/gpt-4o",
            name = "GPT-4o",
            created = 1715558400L,
            description = "High-speed omni model combining fast multimodal intelligence with reliable structured outputs and widespread ecosystem compatibility.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000025", completion = "0.000010"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "audio"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = true),
            supportedParameters = listOf("tools", "json_schema")
        ),
        OpenRouterModelDto(
            id = "google/gemini-2.0-flash",
            name = "Gemini 2.0 Flash",
            created = 1733875200L,
            description = "Blazing fast next-generation multimodal agent engine with sub-second response times, 1M context, and native audio/video understanding.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000010", completion = "0.00000040"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "video", "audio"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = true),
            supportedParameters = listOf("tools", "json_mode")
        ),
        OpenRouterModelDto(
            id = "deepseek/deepseek-v3",
            name = "DeepSeek V3",
            created = 1735171200L,
            description = "671B parameter Mixture-of-Experts powerhouse trained at astonishing efficiency, matching proprietary frontiers at fraction of cost.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000027", completion = "0.0000011"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "meta-llama/llama-3.3-70b-instruct",
            name = "Llama 3.3 70B Instruct",
            created = 1733443200L,
            description = "Efficient enterprise open model rivaling earlier 405B performance with low resource footprints, rapid inference, and high fidelity.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000004", completion = "0.0000004"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 4096, isModerated = false),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "mistralai/mistral-large-2411",
            name = "Mistral Large 2411",
            created = 1731974400L,
            description = "Mistral's premier multilingual foundation model with world-class reasoning, French/German/Spanish fluency, and fine tool orchestration.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.000002", completion = "0.000006"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "cohere/command-r-plus-08-2024",
            name = "Command R+ (08-2024)",
            created = 1723507200L,
            description = "Cohere's state-of-the-art enterprise RAG model with verified citation generation and multi-step external API tool execution.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000025", completion = "0.000010"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 4096, isModerated = false),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "anthropic/claude-3.5-haiku",
            name = "Claude 3.5 Haiku",
            created = 1730678400L,
            description = "Ultra-fast compact model from Anthropic exceeding Opus 3 on coding benchmarks with instantaneous token streaming.",
            contextLength = 200_000,
            pricing = OpenRouterPricingDto(prompt = "0.000001", completion = "0.000005"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = true),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "openai/gpt-4o-mini",
            name = "GPT-4o Mini",
            created = 1721260800L,
            description = "Small, cost-efficient model by OpenAI replacing GPT-3.5 with vision capabilities and significantly higher intelligence.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000015", completion = "0.00000060"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 16384, isModerated = true),
            supportedParameters = listOf("tools", "json_schema")
        ),
        OpenRouterModelDto(
            id = "amazon/nova-pro-v1",
            name = "Amazon Nova Pro",
            created = 1733184000L,
            description = "Amazon's high-capability multimodal model designed for accuracy, speed, and cost efficiency across complex tasks.",
            contextLength = 300_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000008", completion = "0.0000032"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "video"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 5000, isModerated = true),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "microsoft/phi-4",
            name = "Microsoft Phi-4 (14B)",
            created = 1734048000L,
            description = "Compact 14B open-weights model delivering math and reasoning performance rivaling models 5x its parameter count.",
            contextLength = 16_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000015", completion = "0.00000015"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 4096, isModerated = false),
            supportedParameters = listOf("temperature")
        ),
        OpenRouterModelDto(
            id = "google/gemini-flash-1.5-8b",
            name = "Gemini Flash 1.5 8B",
            created = 1727740800L,
            description = "Ultra high-volume, low-cost multimodal model engineered for high-frequency extraction, summarization, and chat agents.",
            contextLength = 1_000_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000000375", completion = "0.00000015"),
            architecture = OpenRouterArchitectureDto(
                modality = "multimodal",
                inputModalities = listOf("text", "image", "video", "audio"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = true),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "mistralai/codestral-2501",
            name = "Codestral 2501",
            created = 1737158400L,
            description = "State-of-the-art coding completion model with fill-in-the-middle support and mastery of 80+ programming languages.",
            contextLength = 256_000,
            pricing = OpenRouterPricingDto(prompt = "0.0000003", completion = "0.0000009"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 8192, isModerated = false),
            supportedParameters = listOf("tools")
        ),
        OpenRouterModelDto(
            id = "meta-llama/llama-3.2-3b-instruct",
            name = "Llama 3.2 3B Instruct",
            created = 1727222400L,
            description = "Ultra-lightweight edge model designed for local on-device execution, quick classification, and low-latency agent tasks.",
            contextLength = 128_000,
            pricing = OpenRouterPricingDto(prompt = "0.00000006", completion = "0.00000006"),
            architecture = OpenRouterArchitectureDto(
                modality = "text",
                inputModalities = listOf("text"),
                outputModalities = listOf("text")
            ),
            topProvider = OpenRouterTopProviderDto(maxCompletionTokens = 4096, isModerated = false),
            supportedParameters = listOf("temperature")
        )
    )
}
