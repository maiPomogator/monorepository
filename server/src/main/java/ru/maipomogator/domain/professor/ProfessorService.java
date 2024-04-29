package ru.maipomogator.domain.professor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepo professorRepo;
    @Deprecated
    private final ProfessorLegacyMapper legacyMapper;

    public List<Professor> findAll() {
        return professorRepo.findAll();
    }

    @Transactional
    public Professor save(Professor professor) {
        return professorRepo.save(professor);
    }

    @Transactional
    public List<Professor> saveAll(Iterable<Professor> professors) {
        return professorRepo.saveAll(professors);
    }

    @Deprecated
    public Optional<ProfessorLegacyDTO> legacyGetOneById(Long id) {
        return professorRepo.findById(id).map(legacyMapper::toLegacyDTO);
    }

    @Deprecated
    public List<ProfessorLegacyDTO> legacyFindAll() {
        return legacyMapper.toDTOs(professorRepo.findAll());
    }

    @Deprecated
    public List<ProfessorLegacyDTO> legacyFindByFio(String fio) {
        return legacyMapper.toDTOs(findByFio(fio));
    }

    private List<Professor> findByFio(String fio) {
        Professor example = new Professor(fio);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Professor> professorExample = Example.of(example, matcher);
        List<Professor> results = professorRepo.findAll(professorExample);
        if (results.isEmpty()) {
            String[] parts = fio.split(" ");
            if (parts.length == 2) {
                example = new Professor();
                example.setFirstName(parts[0]);
                example.setMiddleName(parts[1]);
                professorExample = Example.of(example, matcher);
                results = professorRepo.findAll(professorExample);
            }
        }
        return results;
    }
}
