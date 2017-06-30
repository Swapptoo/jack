package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;

/**
 * Create sub scopes under the execution scope
 */
public class SubScopeCreationExecutor extends BaseExecutor {

  private final Optional<String> newScope;

  SubScopeCreationExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String newScope) {
    super(table, predefinedScope, predefinedScopeNames);
    this.newScope = Optional.of(newScope);
  }

  SubScopeCreationExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
    this.newScope = Optional.empty();
  }

  public JsScope execute(IDb db) throws IOException {
    String scopeName = newScope.orElseGet(() -> UUID.randomUUID().toString());
    return getOrCreateScope(db, getOrCreateExecutionScope(db), Collections.singletonList(scopeName));
  }

}
