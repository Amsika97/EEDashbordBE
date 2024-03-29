package com.maveric.digital.config;

import java.security.SecureRandom;
import java.util.Objects;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.maveric.digital.model.IdentifiedEntity;

@Component
public class IdentifiedEntityEventListener extends AbstractMongoEventListener<IdentifiedEntity> {
	private SecureRandom random = new SecureRandom();
	@Override
	public void onBeforeConvert(BeforeConvertEvent<IdentifiedEntity> event) {
		super.onBeforeConvert(event);
		IdentifiedEntity domain = event.getSource();
		if (Objects.isNull(domain.getId())) {
			domain.setId(Math.abs(random.nextLong(1000000000)));
		}
	}

}
