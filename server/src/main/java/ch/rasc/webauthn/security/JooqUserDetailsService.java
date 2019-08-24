package ch.rasc.webauthn.security;

import static ch.rasc.webauthn.db.tables.AppUser.APP_USER;

import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JooqUserDetailsService implements UserDetailsService {

  private final DSLContext dsl;

  public JooqUserDetailsService(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public UserDetails loadUserByUsername(String userId)
      throws UsernameNotFoundException {

    var appUserRecord = this.dsl.selectFrom(APP_USER)
        .where(APP_USER.ID.eq(Long.valueOf(userId))).limit(1).fetchOne();

    if (appUserRecord != null) {
      return new JooqUserDetails(appUserRecord);
    }

    throw new UsernameNotFoundException(userId);
  }

}
