# FluxMall - Agent Development Guide

## Project Overview
Traditional Spring Framework 5.x shopping mall with JSP views, JdbcTemplate data access, and MySQL database.
External Tomcat deployment with classic MVC architecture and server-side rendering.

**Tech Stack:** Java 11, Spring Framework 5.x, JSP/JSTL/Tiles, JdbcTemplate, Apache Commons DBCP, MySQL 5.7/8.0, Maven

## Build Commands
```bash
# Build WAR for external Tomcat deployment
./mvnw clean package

# Run single test
./mvnw test -Dtest=ClassName#methodName

# Run all tests
./mvnw test

# Skip tests during build
./mvnw clean package -DskipTests
```

**Note:** Deploy generated `target/ROOT.war` to Apache Tomcat 9 webapps directory.

## Project Structure (Target State)
```
com.fluxmall
├── dao/           # Data access objects (JdbcTemplate)
├── service/       # Business logic (@Transactional)
├── controller/    # Spring MVC Controllers (@Controller)
├── dto/           # Data transfer objects
├── vo/            # Value objects (entity models)
├── config/        # Spring configuration (beans, datasource)
├── util/          # Utility classes (logging, date formatting)
└── exception/     # Custom exceptions
```

## Code Style Guidelines

### Naming Conventions
- **Classes**: PascalCase
- **Methods**: camelCase, descriptive verbs (`createOrder`, `findByUsername`, `updateStock`)
- **Variables**: camelCase
- **Constants**: UPPER_SNAKE_CASE
- **Database columns**: snake_case
- **Suffixes**:
  - VOs: `*VO` (MemberVO, ProductVO)
  - DTOs: `*Request`, `*Response`
  - DAOs: `*Dao`
  - Services: `*Service`
  - Controllers: `*Controller`
  - Mappers: `*RowMapper` (if using RowMapper pattern)

### Dependency Injection
Use XML configuration or Java-based configuration:
```xml
<!-- XML-based -->
<bean id="memberService" class="com.fluxmall.service.MemberService">
    <property name="memberDao" ref="memberDao"/>
</bean>

<!-- Or constructor injection with @Autowired (if using annotations) -->
@Service
public class MemberService {
    private final MemberDao memberDao;

    @Autowired
    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }
}
```

### Data Access (DAO)
- Use JdbcTemplate with Apache Commons DBCP connection pool
- Each query method should be focused and atomic
- Use RowMapper or BeanPropertyRowMapper for ResultSet → VO conversion
- Return `null` for "not found"
- SQL as inline strings or external SQL files

```java
@Repository
public class MemberDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public MemberVO findById(Long id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int insert(MemberVO vo) {
        String sql = "INSERT INTO members (username, password) VALUES (?, ?)";
        return jdbcTemplate.update(sql, vo.getUsername(), vo.getPassword());
    }
}
```

### Transaction Management
- Use `@Transactional` annotation from `org.springframework.transaction.annotation`
- Configure transaction manager in Spring config
- Rollback on RuntimeException by default
- Database columns: `created_at`, `updated_at` (timestamps)

### Controller Layer
- Use `@Controller` for view rendering (returns String for JSP path)
- Use `@ResponseBody` for JSON/AJAX responses
- Model attributes: use `model.addAttribute("key", value)`
- Return `"redirect:/path"` for POST-redirect-GET pattern
- Tiles integration for layout management

```java
@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "member/list";  // Maps to /WEB-INF/views/member/list.jsp
    }

    @PostMapping("/register")
    public String register(@ModelAttribute MemberVO member, BindingResult result) {
        if (result.hasErrors()) {
            return "member/register";
        }
        memberService.register(member);
        return "redirect:/member/list";
    }
}
```

### Error Handling
- Custom exceptions in `com.fluxmall.exception`
- Use `@ControllerAdvice` for global exception handling
- DAO: catch exceptions, return `null` for not found
- Service: throw business exceptions
- Controller: use BindingResult for validation errors

### Comments
- JavaDoc style for public methods: `/** Description */`
- Inline comments for complex logic only
- No commented-out code in commits

### Date/Time
- Use `java.time.LocalDateTime` (NOT `java.util.Date`)
- Database columns: `DATETIME` or `TIMESTAMP`
- Format via `DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")`

## Database Connection
Configure DataSource in Spring config (DBCP connection pool):
```
Driver: com.mysql.cj.jdbc.Driver
URL: jdbc:mysql://localhost:3306/shopdb?serverTimezone=Asia/Seoul
User: root
Password: root1234
Max Active: 20
Max Idle: 10
```

## Testing
Use JUnit 4 or 5 with Spring Test:
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Test
    void testFindById() {
        MemberVO member = memberService.findById(1L);
        assertNotNull(member);
    }
}
```

## Commit Convention
Follow `<type>(<scope>): <subject> [#Task_ID]` format:
- **Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- **Example:** `feat(auth): 로그인 서블릿 및 DAO 구현 [#102]`

## Key Constraints
- No Spring Boot – traditional Spring Framework only
- JSP/JSTL/Tiles for views (no Thymeleaf, no template engines)
- JdbcTemplate with Apache Commons DBCP (no JPA/Hibernate)
- External Tomcat deployment (`.war` packaging)
- No REST API framework – Spring MVC with JSP only
- Session-based authentication (no JWT tokens)
- Log4j2 for logging (configure in log4j2.xml)

## Common Patterns to Follow
1. **Controller → Service → DAO** layering is strict
2. DAO handles database operations, Service handles business logic
3. Use Tiles for common layouts (header/footer)
4. Form submissions use POST-redirect-GET pattern
5. Validate inputs at controller level (BindingResult)
6. Use `@Transactional` for multi-step DB operations
7. AJAX endpoints return JSON (`@ResponseBody`)
