package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubRecordInquirer extends BaseInquirerExecutor<JsRecords, Set<Long>, SubRecordInquirer> {

  private final List<GenericConstraint> scopeConstraints = Lists.newArrayList();
  private final Map<String, List<GenericConstraint>> recordConstraints = Maps.newHashMap();
  private final JsTable scope; // alias of table used specifically for scope query and self join

  SubRecordInquirer(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
    this.scope = table.as("scope");
  }

  public SubRecordInquirer whereSubRecordId(IWhereOperator<Long> recordIdConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(scope.idColumn, recordIdConstraint));
    return this;
  }

  public SubRecordInquirer whereSubScopeName(IWhereOperator<String> scopeNameConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(scope.valueColumn, scopeNameConstraint));
    return this;
  }

  public SubRecordInquirer whereSubRecord(String key, IWhereOperator<String> valueConstraint) {
    GenericConstraint constraint = new GenericConstraint<>(table.valueColumn, valueConstraint);
    String queryKey = processKey(key);
    if (this.recordConstraints.containsKey(queryKey)) {
      this.recordConstraints.get(queryKey).add(constraint);
    } else {
      this.recordConstraints.put(queryKey, Lists.newArrayList(constraint));
    }
    return this;
  }

  private static String processKey(String key) {
    String[] paths = key.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR));
    if (paths.length == 1) {
      return key;
    } else {
      return Joiner.on("%" + JsonDbConstants.PATH_SEPARATOR).join(paths) + "%";
    }
  }

  @Override
  JsRecords internalExecute(IDb db) throws IOException {
    Set<Long> validSubRecordIds = internalExec(db);
    return new SubRecordReader(table, executionRecordId, validSubRecordIds).internalExecute(db);
  }

  @Override
  Set<Long> internalExec(IDb db) throws IOException {
    if (recordConstraints.isEmpty()) {
      return querySubScopesByScopeConstraints(db);
    } else {
      return querySubScopesByRecordConstraints(db);
    }
  }

  private Set<Long> querySubScopesByScopeConstraints(IDb db) throws IOException {
    GenericQuery query = db.createQuery()
        .from(scope.table)
        .where(scope.scopeColumn.equalTo(executionRecordId))
        .where(scope.typeColumn.equalTo(ValueType.SCOPE.value))
        .where(scope.keyColumn.equalTo(JsConstants.SCOPE_KEY))
        .select(scope.idColumn);

    for (GenericConstraint constraint : scopeConstraints) {
      query.where(constraint);
    }

    return Sets.newHashSet(query.fetch().gets(scope.idColumn));
  }

  private Set<Long> querySubScopesByRecordConstraints(IDb db) throws IOException {
    Set<Long> recordIds = null;
    for (Map.Entry<String, List<GenericConstraint>> entry : recordConstraints.entrySet()) {
      if (recordIds != null && recordIds.isEmpty()) {
        return Collections.emptySet();
      }

      GenericQuery query = db.createQuery()
          .from(table.table)
          .leftJoin(scope.table)
          .on(table.scopeColumn.equalTo(scope.idColumn))
          .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
          .select(table.scopeColumn);

      // scope constraints
      if (recordIds != null) {
        query.where(table.scopeColumn.in(recordIds));
      }
      for (GenericConstraint constraint : scopeConstraints) {
        query.where(constraint);
      }

      // key constraints
      String key = entry.getKey();
      if (key.contains("%")) {
        query.where(table.keyColumn.matches(key));
      } else {
        query.where(table.keyColumn.equalTo(key));
      }

      // record constraints
      List<GenericConstraint> constraints = entry.getValue();
      for (GenericConstraint constraint : constraints) {
        query.where(constraint);
      }

      recordIds = Sets.newHashSet(query.fetch().gets(table.scopeColumn));
    }

    return recordIds;
  }

  @Override
  SubRecordInquirer getSelf() {
    return this;
  }

}
