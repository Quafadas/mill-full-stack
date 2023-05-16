import { OpenAI } from "langchain/llms/openai";
import { ChatOpenAI } from "langchain/chat_models/openai";
import { BaseChatMessage, HumanChatMessage, SystemChatMessage } from "langchain/schema";
import * as yaml from "js-yaml";
import { JsonSpec, JsonObject } from "langchain/tools";
import { AgentExecutor, createOpenApiAgent, OpenApiToolkit } from "langchain/agents";


export function openAIApiKey() : string {
  const k = import.meta.env.VITE_OPEN_AI_API_KEY;
  return k
}

export function openApiTool(openApiKey: string, toolAccessKey: string, openApiSpecRaw: string):  AgentExecutor {
  
  const dataLocal = yaml.load(openApiSpecRaw) as JsonObject;

  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${toolAccessKey}`,
  };
  const model = new OpenAI({ temperature: 0, openAIApiKey: openApiKey });
  const toolkit = new OpenApiToolkit(new JsonSpec(dataLocal), model, headers);
  return createOpenApiAgent(model, toolkit);

}
