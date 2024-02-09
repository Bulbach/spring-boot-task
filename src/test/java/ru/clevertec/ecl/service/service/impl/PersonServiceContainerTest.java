package ru.clevertec.ecl.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.ecl.PostgresSqlContainerInitialization;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PersonServiceContainerTest extends PostgresSqlContainerInitialization {
}
