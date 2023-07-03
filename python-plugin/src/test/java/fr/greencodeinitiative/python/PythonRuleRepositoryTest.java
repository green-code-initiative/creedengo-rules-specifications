/*
 * Copyright (C) 2023 Green Code Initiative
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.greencodeinitiative.python;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;

public class PythonRuleRepositoryTest {

  private PythonRuleRepository pythonRuleRepository;
  private RulesDefinition.Context context;
  private RulesDefinition.Repository repository;

  @Before
  public void init() {
    pythonRuleRepository = new PythonRuleRepository();
    context = new RulesDefinition.Context();
    pythonRuleRepository.define(context);
    repository = context.repository(PythonRuleRepository.REPOSITORY_KEY);
  }

  @Test
  public void test() {
    assertThat(pythonRuleRepository.repositoryKey()).isEqualTo(PythonRuleRepository.REPOSITORY_KEY);
    assertThat(context.repositories()).hasSize(1).extracting("key").containsExactly(pythonRuleRepository.repositoryKey());
    assertThat(context.repositories().get(0).rules()).hasSize(10);
    assertThat(pythonRuleRepository.checkClasses()).hasSize(10);
  }


  /**
   * Check all rule keys must be prefixed by 'EC'
   */
  @Test
  public void testRuleKeyPrefix() {
    SoftAssertions assertions = new SoftAssertions();
    repository.rules().forEach(
            rule -> assertions.assertThat(rule.key()).startsWith("EC")
    );
    assertions.assertAll();
  }
}
