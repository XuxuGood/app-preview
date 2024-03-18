///*
// * Copyright 2013-2023 the HotswapAgent authors.
// *
// * This file is part of HotswapAgent.
// *
// * HotswapAgent is free software: you can redistribute it and/or modify it
// * under the terms of the GNU General Public License as published by the
// * Free Software Foundation, either version 2 of the License, or (at your
// * option) any later version.
// *
// * HotswapAgent is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// * Public License for more details.
// *
// * You should have received a copy of the GNU General Public License along
// * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
// */
//package org.hotswap.agent.plugin.spring.boot.env.v2;
//
//import org.hotswap.agent.logging.AgentLogger;
//import org.hotswap.agent.plugin.spring.boot.env.BasePropertiesPropertySourceLoader;
//import org.hotswap.agent.plugin.spring.boot.env.ListPropertySourceReloader;
//import org.hotswap.agent.util.ReflectionHelper;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//
//import java.util.List;
//import java.util.Map;
//
//
//public class PropertiesPropertySourceLoader extends BasePropertiesPropertySourceLoader<List<Map<String, ?>>> {
//
//    private static AgentLogger LOGGER = AgentLogger.getLogger(PropertiesPropertySourceLoader.class);
//
//    private PropertiesPropertySourceLoader propertiesPropertySourceLoader;
//    private static Resource originalResource;
//    private static Resource newResource;
//
//    public PropertiesPropertySourceLoader(
//            PropertiesPropertySourceLoader propertiesPropertySourceLoader,
//            String name, Resource resource) {
//        super(new ListPropertySourceReloader(name, resource));
//        this.propertiesPropertySourceLoader = propertiesPropertySourceLoader;
//        PropertiesPropertySourceLoader.originalResource = resource;
//    }
//
//    public static void setResource(String resourcePath) {
//        if (originalResource instanceof ClassPathResource){
//            String path = ((ClassPathResource) originalResource).getPath();
//            if (path.startsWith("/")){
//                path = path.substring(1);
//            }
//        }
//        PropertiesPropertySourceLoader.newResource = new FileSystemResource(resourcePath);
//    }
//
//    /**
//     * spring boot 2.x higher version
//     *
//     * @return
//     */
//    protected List<Map<String, ?>> doLoad() {
//        return (List<Map<String, ?>>) ReflectionHelper.invoke(propertiesPropertySourceLoader, PropertiesPropertySourceLoader.class,
//                "loadProperties", new Class[]{Resource.class}, originalResource);
//    }
//}
