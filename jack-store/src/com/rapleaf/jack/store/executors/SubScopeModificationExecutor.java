package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Joiner;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.MissingScopeException;

/**
 * Modify sub scopes under the execution scope
 */
public class SubScopeModificationExecutor extends BaseExecutor {

  private final String currentName;
  private final String newName;

  SubScopeModificationExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String currentName, String newName) {
    super(table, predefinedScope, predefinedScopeNames);
    this.currentName = currentName;
    this.newName = newName;
  }

  public boolean execute(IDb db) throws IOException {
    Optional<JsScope> executionScope = getOptionalExecutionScope(db);
    if (!executionScope.isPresent()) {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }

    validateOperation(db, executionScope.get());

    Long upperScopeId = executionScope.get().getScopeId();
    return db.createUpdate().table(table.table)
        .set(table.valueColumn, newName)
        .where(table.scopeColumn.equalTo(upperScopeId))
        .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
        .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
        .execute()
        .getUpdatedRowCount() == 1;
  }

  private void validateOperation(IDb db, JsScope executionScope) throws IOException {
    Optional<JsScope> currentScope = getOptionalScope(db, executionScope, Collections.singletonList(currentName));
    if (!currentScope.isPresent()) {
      throw new MissingScopeException(currentName);
    }

    Optional<JsScope> newScope = getOptionalScope(db, executionScope, Collections.singletonList(newName));
    if (newScope.isPresent()) {
      throw new JackRuntimeException(String.format("Scope %s already exists", newName));
    }
  }

}
