# 🚀 BlackRock Auto-Savings & Returns Engine

Production-grade backend system built using **Java 21** and **Spring Boot 3.5.x**

This application implements an **automated retirement micro-savings platform** based on:

✔ Expense rounding strategy  
✔ Temporal financial constraints (Q / P / K periods)  
✔ Investment return calculations  
✔ Inflation adjustment  
✔ NPS tax benefit computation  
✔ Runtime performance reporting

Designed to support **large-scale financial workloads**.

---

# 📌 Challenge Overview

Retirement planning remains a critical challenge where savings behavior is inconsistent.  
Behavioral economics suggests that **automated micro-savings via expense rounding** significantly improves long-term accumulation.

This system operationalizes:

✅ Expense → Transaction transformation  
✅ Temporal constraint processing  
✅ Investment projection modelling  
✅ Inflation-adjusted real returns  
✅ Tax incentive simulation

As defined in the challenge specification.

---

# 🏗️ Architecture

Enterprise-style layered design:

| Layer | Responsibility |
|------|---------------|
| **API (Controllers)** | REST endpoints |
| **Application Services** | Orchestration |
| **Computation Engines** | Financial logic |
| **Validators** | Business rule enforcement |
| **Strategies / Calculators** | Extensible calculations |
| **Infrastructure** | Exceptions / Configuration |

---

# 🎯 Engineering Principles

✔ SOLID design  
✔ Stateless processing  
✔ Clean separation of concerns  
✔ DTO-driven contracts  
✔ High testability  
✔ Production-grade exception handling

---

# 🧠 Design Patterns Used

### ✅ Strategy Pattern
Used for:

- Investment return models (NPS vs Index Fund)

---

### ✅ Calculator Pattern
Used for:

- Compound interest calculations
- Inflation adjustments
- Profit computation

---

### ✅ Validator Pattern
Used for:

- Financial integrity checks
- Business rule enforcement

---

### ✅ Factory Pattern
Used for:

- Temporal rule resolution strategies

---

# ⚙️ Features & APIs

---

## 💳 1. Transaction Builder

**POST**  
`/blackrock/challenge/v1/transactions:parse`

**Description**

Transforms expenses into transactions:

- Ceiling → Next multiple of 100
- Remanent → Investable difference

---

## 🛡️ 2. Transaction Validator

**POST**  
`/blackrock/challenge/v1/transactions:validator`

**Description**

Validates:

✔ Negative values  
✔ Invalid remanent  
✔ Ceiling inconsistencies  
✔ Duplicate timestamps

Returns:

- Valid transactions
- Invalid transactions
- Duplicate transactions

---

## ⏳ 3. Temporal Constraints Engine

**POST**  
`/blackrock/challenge/v1/transactions:filter`

**Description**

Applies:

✔ **Q Periods** → Fixed remanent override  
✔ **P Periods** → Extra savings addition

**Rule Precedence**

---

## 📈 4. Returns – NPS

**POST**  
`/blackrock/challenge/v1/returns:nps`

**Description**

Calculates:

✔ Invested amount  
✔ Compound returns  
✔ Profit  
✔ Inflation-adjusted value  
✔ Tax benefit

Constraints:

- Max ₹2,00,000 deduction
- 10% annual income cap

---

## 📊 5. Returns – Index Fund

**POST**  
`/blackrock/challenge/v1/returns:index`

**Description**

Calculates:

✔ Compound returns  
✔ Profit  
✔ Inflation-adjusted value

(No tax benefit)

---

## 📊 6. Performance Metrics

**GET**  
`/blackrock/challenge/v1/performance`

**Description**

Reports:

✔ Execution time  
✔ Heap memory usage  
✔ Active thread count

---

# 💰 Financial Calculations

---

## 📌 Remanent Formula
ceiling = ceil(amount / 100) × 100
remanent = ceiling − amount
---

## 📌 Compound Interest
A = P × (1 + r)^t


Where:

- **P** → Invested remanent
- **r** → Annual return rate
- **t** → Years until retirement

---

## 📌 Inflation Adjustment
Real Value = A / (1 + inflation)^t


---

## 📌 NPS Tax Benefit
Deduction = min(invested, 10% annual income, ₹200000)
Tax Benefit = Tax(before) − Tax(after deduction)


---

# 🚀 Running the Application

---

## ✅ Local Execution

**Requirements**

- Java 21
- Maven

```bash
mvn clean package
mvn spring-boot:run

http://localhost:5477
```

# Docker Execution
```
docker pull juyel8968/blk-hacking-ind-juyel:latest
docker run -d -p 5477:5477 juyel8968/blk-hacking-ind-juyel

Application runs on:
http://localhost:5477
```

# Docker Image
## Available publicly on Docker Hub:
```
juyel8968/blk-hacking-ind-juyel
```

## API Documentation (Swagger / OpenAPI)
# Swagger UI available at:
``
http://localhost:5477/swagger-ui/index.html``

## 📦 Postman Collection
Postman collection available at:
```
/postman/BlackRock-Challenge.postman_collection.json
```



