import { Link } from "react-router-dom"
import { Separator } from "@/components/ui/separator"
import { env } from "@/lib/env"

const LAST_UPDATED = "October 8, 2026"
const CONTACT_EMAIL = "privacy@rentrix.app"

export default function PrivacyPage() {
  return (
    <article className="container mx-auto max-w-3xl px-4 py-12">
      <header className="mb-8 space-y-2">
        <h1 className="text-3xl font-bold tracking-tight">Privacy Policy</h1>
        <p className="text-sm text-muted-foreground">Last updated: {LAST_UPDATED}</p>
      </header>

      <div className="max-w-none space-y-8 text-sm leading-relaxed text-muted-foreground">
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">1. Introduction</h2>
          <p>
            {env.VITE_APP_NAME} ("we", "us", "our") operates a rental review platform that lets
            tenants share honest reviews of flats they have lived in, and helps renters make
            informed decisions. This Privacy Policy explains how we collect, use, and protect your
            personal data when you use our services at{" "}
            <span className="text-foreground">myrentrix.vercel.app</span> (the "Service").
          </p>
          <p>
            We are currently operated as an independent project, not a registered business entity.
            If you have questions about this policy, contact us at{" "}
            <a
              href={`mailto:${CONTACT_EMAIL}`}
              className="text-foreground underline underline-offset-4 hover:text-primary"
            >
              {CONTACT_EMAIL}
            </a>
            .
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">2. Data We Collect</h2>
          <p>We collect the following categories of information:</p>

          <div className="space-y-3">
            <div>
              <p className="font-medium text-foreground">Account information</p>
              <p>
                Name, email address, and password (stored as a salted hash). You may optionally
                provide a phone number, date of birth, and address in your profile.
              </p>
            </div>

            <div>
              <p className="font-medium text-foreground">Content you create</p>
              <p>
                Reviews, ratings (1–10), flat details you add (address, rent, property type,
                photographs, proof-of-living documents), and any messages you send to us.
              </p>
            </div>

            <div>
              <p className="font-medium text-foreground">Usage data</p>
              <p>
                IP address, browser type, pages visited, timestamps, and referring URLs. Collected
                automatically by our hosting providers (Vercel, Render) and by cookies required for
                authentication.
              </p>
            </div>

            <div>
              <p className="font-medium text-foreground">Cookies and local storage</p>
              <p>
                We store: (a) an authentication token in localStorage to keep you signed in; (b)
                your theme preference (dark/light). We do not use third-party advertising or
                tracking cookies.
              </p>
            </div>
          </div>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">3. How We Use Your Data</h2>
          <ul className="list-disc space-y-1 pl-5">
            <li>To create and secure your account, and authenticate you.</li>
            <li>
              To publish your reviews and ratings publicly, associated with your display name.
            </li>
            <li>To display flats you add to the platform.</li>
            <li>To moderate content for safety and accuracy.</li>
            <li>To detect abuse, spam, and fraudulent reviews.</li>
            <li>To send transactional emails (e.g., password reset, moderation outcomes).</li>
            <li>To improve the Service based on aggregate usage patterns.</li>
          </ul>
          <p>We do not sell your personal data. We do not use your data for advertising.</p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">4. Legal Bases (GDPR)</h2>
          <p>If you are in the European Economic Area, we process your data on these bases:</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>
              <span className="text-foreground">Contract</span> — to provide the Service you signed
              up for.
            </li>
            <li>
              <span className="text-foreground">Legitimate interests</span> — to prevent abuse,
              ensure platform integrity, and improve the Service.
            </li>
            <li>
              <span className="text-foreground">Consent</span> — for optional features you opt into
              (e.g., marketing emails, if ever introduced).
            </li>
            <li>
              <span className="text-foreground">Legal obligation</span> — when we must retain data
              to comply with applicable law.
            </li>
          </ul>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">5. Sharing Your Data</h2>
          <p>We share data only with service providers who help us run the platform:</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>
              <span className="text-foreground">Vercel</span> — frontend hosting (may process IP
              addresses and access logs).
            </li>
            <li>
              <span className="text-foreground">Render</span> — backend hosting (may process IP
              addresses and access logs).
            </li>
            <li>
              <span className="text-foreground">Aiven</span> — managed database hosting (stores all
              persistent data on encrypted storage).
            </li>
            <li>
              <span className="text-foreground">Cloudflare R2</span> — object storage for images and
              documents (once file uploads are enabled).
            </li>
          </ul>
          <p>
            We do not share data with advertisers, data brokers, or any third party for marketing
            purposes.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">6. International Transfers</h2>
          <p>
            Our service providers may store your data in the United States, the European Union, or
            other regions. Where required, transfers are covered by Standard Contractual Clauses or
            equivalent safeguards.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">7. Data Retention</h2>
          <p>
            We retain your account and content for as long as your account is active. When you
            delete your account, we remove personal data within 30 days, except where retention is
            required by law or necessary to resolve disputes. Anonymized reviews may remain
            published after account deletion to preserve platform history.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">8. Your Rights</h2>
          <p>
            Depending on your location, you may have the following rights. To exercise any of them,
            email{" "}
            <a
              href={`mailto:${CONTACT_EMAIL}`}
              className="text-foreground underline underline-offset-4 hover:text-primary"
            >
              {CONTACT_EMAIL}
            </a>
            . We respond within 30 days.
          </p>

          <div className="space-y-3">
            <div>
              <p className="font-medium text-foreground">Under GDPR (EU/EEA)</p>
              <p>
                Access, rectification, erasure, restriction, portability, and objection. You may
                also lodge a complaint with your local data protection authority.
              </p>
            </div>

            <div>
              <p className="font-medium text-foreground">Under DPDP Act, 2023 (India)</p>
              <p>
                Access, correction, and erasure of your personal data. You may nominate a person to
                exercise these rights on your behalf in the event of death or incapacity.
              </p>
            </div>

            <div>
              <p className="font-medium text-foreground">Under CCPA (California)</p>
              <p>
                Know what personal information we collect, request deletion, and opt out of "sale"
                (we do not sell personal information). You may designate an authorized agent.
              </p>
            </div>
          </div>

          <p>
            You can also delete your account and content at any time from your account settings, or
            by emailing us.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">9. Children</h2>
          <p>
            The Service is not directed to children under 18. We do not knowingly collect data from
            children. If you believe a child has provided us data, contact us and we will delete it.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">10. Security</h2>
          <p>
            Passwords are hashed with BCrypt. All traffic is served over HTTPS. Data at rest is
            encrypted by our hosting providers. No system is perfectly secure — if you believe your
            account has been compromised, contact us immediately.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">11. Changes to This Policy</h2>
          <p>
            We may update this policy. Material changes will be communicated by email or a prominent
            notice on the Service. The "Last updated" date at the top of this page reflects the most
            recent revision.
          </p>
        </section>
        <Separator className="my-8" />
        <p className="text-xs">
          See also our{" "}
          <Link
            to="/terms"
            className="text-foreground underline underline-offset-4 hover:text-primary"
          >
            Terms of Service
          </Link>
          .
        </p>
      </div>
    </article>
  )
}