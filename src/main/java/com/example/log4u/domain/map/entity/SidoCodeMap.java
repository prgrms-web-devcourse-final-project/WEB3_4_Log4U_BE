package com.example.log4u.domain.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "province_code_map", schema = "public")
public class SidoCodeMap {

	@Id
	@Column(length = 2)
	private String code;

	@Column(nullable = false)
	private String name;
}
