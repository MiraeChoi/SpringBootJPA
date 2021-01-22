package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Autowired MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("damgomA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("damgom1");
        Member member2 = new Member("damgom2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //findMember1.setUsername("my Damgom!!!!!!!!!!!!!!!!!!!!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("damgomA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("damgomA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("damgomA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("damgomA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("damgomA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("damgomA", "damgomB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("damgomA", 10);
        Member m2 = new Member("damgomB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //List<Member> result = memberRepository.findListByUsername("no data");
        //Member findMember = memberRepository.findMemberByUsername("no data");
        Optional<Member> findMember = memberRepository.findOptionalByUsername("damgomA");
        System.out.println("findMember = " + findMember);
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("damgom1", 10));
        memberRepository.save(new Member("damgom2", 10));
        memberRepository.save(new Member("damgom3", 10));
        memberRepository.save(new Member("damgom4", 10));
        memberRepository.save(new Member("damgom5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("damgom1", 10));
        memberRepository.save(new Member("damgom2", 19));
        memberRepository.save(new Member("damgom3", 20));
        memberRepository.save(new Member("damgom4", 21));
        memberRepository.save(new Member("damgom5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear();

        List<Member> result = memberRepository.findByUsername("damgom5");
        Member damgom5 = result.get(0);
        System.out.println("damgom5 = " + damgom5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //damgom1 -> teamA
        //damgom2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member damgom1 = new Member("damgom1", 10, teamA);
        Member damgom2 = new Member("damgom2", 10, teamB);
        memberRepository.save(damgom1);
        memberRepository.save(damgom2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("damgom1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member damgom1 = new Member("damgom1", 10);
        memberRepository.save(damgom1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("damgom1");
        findMember.setUsername("damgomdamgom");
        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member damgom1 = new Member("damgom1", 10);
        memberRepository.save(damgom1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("damgom1");
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("gom1", 0, teamA);
        Member m2 = new Member("gom2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.userName("gom1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("gom1", 0, teamA);
        Member m2 = new Member("gom2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("gom1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("gom1");
    }

    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("gom1", 0, teamA);
        Member m2 = new Member("gom2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("gom1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }
    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("gom1", 0, teamA);
        Member m2 = new Member("gom2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();

        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }
}

















