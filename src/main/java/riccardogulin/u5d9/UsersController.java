package riccardogulin.u5d9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d9.entities.User;
import riccardogulin.u5d9.exceptions.BadRequestException;
import riccardogulin.u5d9.payloads.NewUserDTO;
import riccardogulin.u5d9.services.UsersService;

import java.util.UUID;
import java.util.stream.Collectors;

/*

1. GET http://localhost:3001/users
2. POST http://localhost:3001/users (+ req.body) --> 201
3. GET http://localhost:3001/users/{userId}
4. PUT http://localhost:3001/users/{userId} (+ req.body)
5. DELETE http://localhost:3001/users/{userId} --> 204

*/


@RestController
@RequestMapping("/users")
public class UsersController {
	@Autowired
	private UsersService usersService;

	// 1. GET http://localhost:3001/users
	@GetMapping
	public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
	                          @RequestParam(defaultValue = "id") String sortBy) {
		// Mettiamo dei valori di default per far si che non ci siano errori se il client non ci invia uno dei query parameters
		return this.usersService.findAll(page, size, sortBy);
	}

	// 2. POST http://localhost:3001/users (+ req.body) --> 201
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User save(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		// @Validated serve per "attivare" le regole di validazione descritte nel DTO
		// BindingResult contiene l'esito della validazione, quindi sarà utile per capire se ci sono stati errori e quali essi siano
		if (validationResult.hasErrors()) {
			String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage())
					.collect(Collectors.joining(". "));
			throw new BadRequestException("Ci sono stati errori nel payload! " + message);
		}

		return this.usersService.save(body);
	}


	// 3. GET http://localhost:3001/users/{userId}
	@GetMapping("/{userId}")
	public User findById(@PathVariable UUID userId) {
		return this.usersService.findById(userId);
	}


	// 4. PUT http://localhost:3001/users/{userId} (+ req.body)
	@PutMapping("/{userId}")
	public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			validationResult.getAllErrors().forEach(System.out::println);
			throw new BadRequestException("Ci sono stati errori nel payload!");
		}
		// Ovunque ci sia un body bisognerebbe validarlo!
		return this.usersService.findByIdAndUpdate(userId, body);
	}


	// 5. DELETE http://localhost:3001/users/{userId} --> 204
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void findByIdAndDelete(@PathVariable UUID userId) {
		this.usersService.findByIdAndDelete(userId);
	}
}
