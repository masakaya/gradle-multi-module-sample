package com.masakaya.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Persistence Layer Configuration
 * persistence-moduleのコンポーネントをSpring管理下に置くための設定
 */
@Configuration
@ComponentScan(basePackages = ["com.masakaya.infrastructure.persistence"])
class PersistenceConfig