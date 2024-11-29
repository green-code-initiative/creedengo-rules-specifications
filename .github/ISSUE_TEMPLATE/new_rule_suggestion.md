---
name: New rule suggestion
about: Suggest an new rule idea for this project
title: ''
labels: 'rule'
assignees: ''

---

# {Category: RuleTitle (Variant)}

## Platform

| OS | OS version | Langage |
|---------------|--------------|------------|
| {Android/IOS} | {OS version} | {Language} |

## Main caracteristics

| ID | Title | Category | Sub-category |
|----------|----------------------|-------------|----------------|
| {id} | {title} | {Category} | {SubCategory} |

## Severity / Remediation Cost

- **Case 1**:
  | Severity | Remediation Cost |
  |------------|---------------------|
  | {Severity} | {Remediation_Cost} |
- **Case 2**:
  | Severity | Remediation Cost |
  |------------|---------------------|
  | {Severity} | {Remediation_Cost} |

## Rule short description

- **Case 1**: {short description}
- **Case 2**: {short description}

## Rule complete description

## Text

{big description}

## HTML

 ```html
{html code}
```

## Implementation principle

- {Implementation principe}
- {Implementation principe}
  17 h 17
  voici le template
  17 h 17
  et voici un example :
  17 h 17

# Optimized API: List Shallow Copy - Module copy

## Platform

| OS | OS version | Langage |
|----------|------------|-----------|
| - | - | Python |

## Main caracteristics

| ID | Title | Category | Sub-category |
|---------|----------------------------------|-------------|----------------|
| EOPT001 | List Shallow Copy - Module copy | Environment | Optimized API |

## Severity / Remediation Cost

| Severity | Remediation Cost |
|----------|------------------|
| Minor | Minor |

## Rule short description

Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.

## Rule complete description

### Text

Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.
Prefer the usage of `list.copy()` which is more energy friendly.

### HTML

 ```html
<p>Using <code>copy.copy(x)</code> of <code>module copy</code> to perform a shallow
copy of a list is not energy efficient.</p>
<p>Prefer the usage of <code>list.copy()</code> which is more energy friendly.</p>
<h2>Noncompliant Code Example</h2>
<pre>
import copy
 my_list = [1, 2, [3, 4], 5]
list_copy = copy.copy(my_list)
</pre>
<h2>Compliant Solution</h2>
<pre>
my_list = [1, 2, [3, 4], 5]
list_copy = my_list.copy()
</pre>
```

## Implementation principle

- Inspect the import node to find `copy` import
- Inspect the ARG_LIST node
- If the direct parent is CALL_EXPR tree and the function is `copy.copy()`
- If the function is present, check the first argument
- If it is a list, report the line